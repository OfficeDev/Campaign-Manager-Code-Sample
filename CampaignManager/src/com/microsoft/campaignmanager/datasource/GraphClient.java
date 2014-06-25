package com.microsoft.campaignmanager.datasource;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.office365.Credentials;
import com.microsoft.office365.LogLevel;
import com.microsoft.office365.Logger;
import com.microsoft.office365.Platform;
import com.microsoft.office365.Query;
import com.microsoft.office365.http.HttpConnection;
import com.microsoft.office365.http.Request;
import com.microsoft.office365.http.Response;

public class GraphClient {

	Credentials mCredentials;
	String mTenantId;
	Logger mLogger;
	String mTag = "GraphClient";
	String mApiVersion = "api-version=2013-11-08";

	public GraphClient(Credentials credentials, String tenantId) {
		this(credentials, tenantId, null);
	}

	public GraphClient(Credentials credentials, String tenantId, Logger logger) {
		if (credentials == null) {
			throw new IllegalArgumentException("credentials must not be null");
		}
		if (tenantId == null) {
			throw new IllegalArgumentException("tenantId must not be null");
		}

		if (logger == null) {
			// add an empty logger
			mLogger = new Logger() {

				@Override
				public void log(String message, LogLevel level) {
				}
			};
		} else {
			mLogger = logger;
		}

		mCredentials = credentials;
		mTenantId = tenantId;
	}

	protected void log(String message, LogLevel level) {
		getLogger().log(message, level);
	}

	protected void log(Throwable error) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		error.printStackTrace(pw);
		String stackTrace = sw.toString();

		getLogger().log(error.toString() + "\nStack Trace: " + stackTrace, LogLevel.Critical);
	}

	protected Logger getLogger() {
		return mLogger;
	}

	protected Credentials getCredentials() {
		return mCredentials;
	}

	protected String generateODataQueryString(Query query) {
		StringBuilder sb = new StringBuilder();
		if (query != null) {
			query.ensureIdProperty();
			sb.append("$filter=");
			sb.append(queryEncode(query.toString()));

			String rowSetModifiers = query.getRowSetModifiers().trim();
			if (rowSetModifiers != "") {

				if (!rowSetModifiers.startsWith("&")) {
					sb.append("&");
				}
				sb.append(rowSetModifiers);
			}
		}

		return sb.toString();
	}

	protected ListenableFuture<byte[]> executeRequest(String url, String method) {
		return executeRequest(url, method, null, null);
	}

	protected ListenableFuture<byte[]> executeRequest(String url, String method, Map<String, String> headers,
			byte[] payload) {
		HttpConnection connection = Platform.createHttpConnection();

		Request request = new Request(method);

		if (headers != null) {
			for (String key : headers.keySet()) {
				request.addHeader(key, headers.get(key));
			}
		}

		request.setUrl(url);
		request.setContent(payload);
		prepareRequest(request);

		log("Generate request for " + url, LogLevel.Verbose);
		request.log(getLogger());

		final SettableFuture<byte[]> result = SettableFuture.create();
		final ListenableFuture<Response> future = connection.execute(request);

		Futures.addCallback(future, new FutureCallback<Response>() {
			@Override
			public void onFailure(Throwable t) {
				result.setException(t);
			}

			@Override
			public void onSuccess(Response response) {
				try {
					int statusCode = response.getStatus();
					if (isValidStatus(statusCode)) {
						byte[] responseContentBytes = response.readAllBytes();
						result.set(responseContentBytes);
					} else {
						result.setException(new Exception("Invalid status code " + statusCode + ": "
								+ response.readToEnd()));

					}
				} catch (IOException e) {
					log(e);
				}
			}
		});
		return result;
	}

	protected ListenableFuture<JSONObject> executeRequestJson(String url, String method) {
		return executeRequestJson(url, method, null, null);
	}

	protected ListenableFuture<JSONObject> executeRequestJson(String url, String method, Map<String, String> headers,
			byte[] payload) {

		final SettableFuture<JSONObject> result = SettableFuture.create();
		final ListenableFuture<byte[]> request = executeRequest(url, method, headers, payload);

		Futures.addCallback(request, new FutureCallback<byte[]>() {
			@Override
			public void onFailure(Throwable t) {
				result.setException(t);
			}

			@Override
			public void onSuccess(byte[] b) {
				String string;
				try {
					string = new String(b, GraphConstants.UTF8_NAME);
					if (string == null || string.length() == 0) {
						result.set(null);
					} else {
						JSONObject json = new JSONObject(string);
						result.set(json);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		return result;
	}

	protected void prepareRequest(Request request) {
		request.addHeader("Accept", "application/json;odata=minimalmetadata");
		request.addHeader("X-ClientService-ClientTag", "SDK-JAVA");
		request.addHeader("Prefer", "return-content");
		request.addHeader("Content-Type", "application/json");

		int contentLength = 0;
		if (request.getContent() != null) {
			contentLength = request.getContent().length;
		}
		request.addHeader("Content-Length", String.valueOf(contentLength));
		mCredentials.prepareRequest(request);
	}

	protected static boolean isValidStatus(int status) {
		return status >= 200 && status <= 299;
	}

	protected String queryEncode(String query) {

		String encoded = null;

		try {
			encoded = query.replaceAll("\\s", "+");
		} catch (Exception e) {
			encoded = query;
		}
		return encoded;
	}

	protected String urlEncode(String str) {
		String encoded = null;
		try {
			encoded = URLEncoder.encode(str, GraphConstants.UTF8_NAME);
		} catch (UnsupportedEncodingException e) {
			encoded = str;
		}

		encoded = encoded.replaceAll("\\+", "%20").replaceAll("\\%21", "!").replaceAll("\\%27", "'")
				.replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\%7E", "~");
		return encoded;
	}

	protected String UUIDtoString(UUID id) {
		return id.toString().replace("-", "");
	}

	private byte[] getBytes(String s) {
		try {
			return s.getBytes(GraphConstants.UTF8_NAME);
		} catch (UnsupportedEncodingException e) {
			return s.getBytes();
		}
	}

	//
	// Operations on Graph API Objects
	//
	public ListenableFuture<GraphApplication> createApplication(final GraphApplication application) {
		final SettableFuture<GraphApplication> result = SettableFuture.create();

		String graphResource = String.format("applications?%s", mApiVersion);
		String postApplicationUrl = String.format("%s/%s/%s", GraphConstants.AAD_GRAPHURL, mTenantId, graphResource);

		try {
			String payloadString = "{\"displayName\":\"" + application.getDisplayName() 
					+ "\",\"replyUrls\":[\"http://home\"],\"publicClient\":true}";

			ListenableFuture<JSONObject> request = executeRequestJson(postApplicationUrl, "POST", null,
			getBytes(payloadString));

			Futures.addCallback(request, new FutureCallback<JSONObject>() {
				@Override
				public void onFailure(Throwable t) {
					result.setException(t);
				}

				@Override
				public void onSuccess(JSONObject json) {
					try {
						//POST to applications will return a single Graph Application
						GraphApplication newApp = new GraphApplication();
						newApp.loadFromJson(json);
						result.set(newApp);
					} catch (Exception e) {
						Log.e(mTag, e.getMessage());
					}
				}
			});
		} catch (Throwable t) {
			result.setException(t);
		}

		return result;
	}
	
	public ListenableFuture<GraphServicePrincipal> createServicePrincipal(final GraphApplication application) {
		final SettableFuture<GraphServicePrincipal> result = SettableFuture.create();

		String graphResource = String.format("servicePrincipals?%s", mApiVersion);
		String postServicePrincipalUrl = String.format("%s/%s/%s", GraphConstants.AAD_GRAPHURL, mTenantId, graphResource);

		try {
			String payloadString = "{\"displayName\":\"" + application.getDisplayName() + "\","
					+"\"appId\":\"" + application.getAppId() +"\","
					+"\"tags\":[\"windowsAzureActiveDirectoryIntegratedApp\"],\"accountEnabled\":true}";

			ListenableFuture<JSONObject> request = executeRequestJson(postServicePrincipalUrl, "POST", null,
			getBytes(payloadString));

			Futures.addCallback(request, new FutureCallback<JSONObject>() {
				@Override
				public void onFailure(Throwable t) {
					result.setException(t);
				}

				@Override
				public void onSuccess(JSONObject json) {
					try {
						GraphServicePrincipal newServicePrincipal = new GraphServicePrincipal();
						newServicePrincipal.loadFromJson(json);
						result.set(newServicePrincipal);
					} catch (Exception e) {
						Log.e(mTag, e.getMessage());
					}
				}
			});
		} catch (Throwable t) {
			result.setException(t);
		}
		return result;
	}
	
	public ListenableFuture<GraphPermission> createPermission(GraphPermission permission) {
		final SettableFuture<GraphPermission> result = SettableFuture.create();

		String graphResource = String.format("permissions?%s", mApiVersion);
		String postPermissionUrl = String.format("%s/%s/%s", GraphConstants.AAD_GRAPHURL, mTenantId, graphResource);

		try {
			String payloadString = "{\"consentType\":\"" + permission.getConsentType() + "\","
					+"\"clientId\":\"" + permission.getClientId() +"\","
					+"\"startTime\":\"" + permission.getStartTimeOdata() +"\","
					+"\"expiryTime\":\"" + permission.getExpiryTimeOdata() +"\","
					+"\"resourceId\":\"" + permission.getResourceId() +"\","
					+"\"scope\":\"" + permission.getScope() +"\"}";

			ListenableFuture<JSONObject> request = executeRequestJson(postPermissionUrl, "POST", null,
			getBytes(payloadString));

			Futures.addCallback(request, new FutureCallback<JSONObject>() {
				@Override
				public void onFailure(Throwable t) {
					result.setException(t);
				}

				@Override
				public void onSuccess(JSONObject json) {
					try {
						GraphPermission newPermission = new GraphPermission();
						newPermission.loadFromJson(json);
						result.set(newPermission);
					} catch (Exception e) {
						Log.e(mTag, e.getMessage());
					}
				}
			});
		} catch (Throwable t) {
			result.setException(t);
		}
		return result;
	}

}
