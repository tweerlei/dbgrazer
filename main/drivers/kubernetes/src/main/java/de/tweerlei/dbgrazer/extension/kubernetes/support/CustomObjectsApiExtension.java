/*
 * Copyright 2018 tweerlei Wruck + Buchmeier GbR - http://www.tweerlei.de/
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tweerlei.dbgrazer.extension.kubernetes.support;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import io.kubernetes.client.ApiCallback;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.ApiResponse;
import io.kubernetes.client.Pair;
import io.kubernetes.client.ProgressRequestBody;
import io.kubernetes.client.ProgressResponseBody;
import io.kubernetes.client.apis.CustomObjectsApi;
import io.kubernetes.client.models.V1APIResourceList;

/**
 * CustomObjectsApi extension that supports getAPIResources
 * 
 * @author Robert Wruck
 */
public class CustomObjectsApiExtension extends CustomObjectsApi
	{
	/**
	 * Constructor
	 * @param apiClient ApiClient
	 */
	public CustomObjectsApiExtension(ApiClient apiClient)
		{
		super(apiClient);
		}
	
	public Call getAPIResourcesCall(String group, String version, final ProgressResponseBody.ProgressListener progressListener, ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException
		{
		Object localVarPostBody = null;
		String localVarPath = "/apis/{group}/{version}/".replaceAll("\\{group\\}", this.getApiClient().escapeString(group.toString())).replaceAll("\\{version\\}", this.getApiClient().escapeString(version.toString()));
		List<Pair> localVarQueryParams = new ArrayList<Pair>();
		List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
		Map<String, String> localVarHeaderParams = new HashMap<String, String>();
		Map<String, Object> localVarFormParams = new HashMap<String, Object>();
		String[] localVarAccepts = new String[]{"application/json", "application/yaml", "application/vnd.kubernetes.protobuf"};
		String localVarAccept = this.getApiClient().selectHeaderAccept(localVarAccepts);
		if (localVarAccept != null)
			{
			localVarHeaderParams.put("Accept", localVarAccept);
			}

		String[] localVarContentTypes = new String[]{"application/json", "application/yaml", "application/vnd.kubernetes.protobuf"};
		String localVarContentType = this.getApiClient().selectHeaderContentType(localVarContentTypes);
		localVarHeaderParams.put("Content-Type", localVarContentType);
		if (progressListener != null)
			{
			this.getApiClient().getHttpClient().networkInterceptors().add(new Interceptor()
				{
				@Override
				public Response intercept(Chain chain) throws IOException
					{
					Response originalResponse = chain.proceed(chain.request());
					return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), progressListener)).build();
					}
				});
			}

		String[] localVarAuthNames = new String[]{"BearerToken"};
		return this.getApiClient().buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, progressRequestListener);
		}

	private Call getAPIResourcesValidateBeforeCall(String group, String version, ProgressResponseBody.ProgressListener progressListener, ProgressRequestBody.ProgressRequestListener progressRequestListener) throws ApiException
		{
		Call call = this.getAPIResourcesCall(group, version, progressListener, progressRequestListener);
		return call;
		}

	public V1APIResourceList getAPIResources(String group, String version) throws ApiException
		{
		ApiResponse<V1APIResourceList> resp = this.getAPIResourcesWithHttpInfo(group, version);
		return resp.getData();
		}

	public ApiResponse<V1APIResourceList> getAPIResourcesWithHttpInfo(String group, String version) throws ApiException
		{
		Call call = this.getAPIResourcesValidateBeforeCall(group, version, (ProgressResponseBody.ProgressListener) null, (ProgressRequestBody.ProgressRequestListener) null);
		Type localVarReturnType = (new TypeToken<V1APIResourceList>()
			{
			}).getType();
		return this.getApiClient().execute(call, localVarReturnType);
		}

	public Call getAPIResourcesAsync(String group, String version, final ApiCallback<V1APIResourceList> callback) throws ApiException
		{
		ProgressResponseBody.ProgressListener progressListener = null;
		ProgressRequestBody.ProgressRequestListener progressRequestListener = null;
		if (callback != null)
			{
			progressListener = new ProgressResponseBody.ProgressListener()
				{
				@Override
				public void update(long bytesRead, long contentLength, boolean done)
					{
					callback.onDownloadProgress(bytesRead, contentLength, done);
					}
				};
			progressRequestListener = new ProgressRequestBody.ProgressRequestListener()
				{
				@Override
				public void onRequestProgress(long bytesWritten, long contentLength, boolean done)
					{
					callback.onUploadProgress(bytesWritten, contentLength, done);
					}
				};
			}

		Call call = this.getAPIResourcesValidateBeforeCall(group, version, progressListener, progressRequestListener);
		Type localVarReturnType = (new TypeToken<V1APIResourceList>()
			{
			}).getType();
		this.getApiClient().executeAsync(call, localVarReturnType, callback);
		return call;
		}
	}
