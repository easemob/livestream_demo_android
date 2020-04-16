package com.easemob.livedemo.common;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Created by songyuqiang on 16/11/3.
 */
public class LoggerInterceptor implements Interceptor{
    public static final String TAG = "OkHttpResponse";
    private String mTag;
    private boolean mShowResponse;
    private boolean mShowRequest;

    public LoggerInterceptor(@NonNull String tag, boolean showResponse, boolean showRequest){
        mShowRequest = showRequest;
        if(TextUtils.isEmpty(tag)){
            mTag = TAG;
        }else {
            this.mTag = tag;
        }
        this.mShowResponse = showResponse;

    }

    @Override
    public Response intercept(Chain chain) throws IOException {


        Request request = chain.request();
        if(mShowRequest){
            logForRequest(request);
        }
        Response response = chain.proceed(request);
        return logForResponse(response);
    }
    private Response logForResponse(Response response)
    {
        if(!mShowResponse){
            return response;
        }

        try
        {
            //===>response log
            Log.e(mTag, "========response'log=======");
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();
            Log.e(mTag, "url : " + clone.request().url());
            Log.e(mTag, "code : " + clone.code());
            Log.e(mTag, "protocol : " + clone.protocol());
            if (!TextUtils.isEmpty(clone.message()))
                Log.e(mTag, "message : " + clone.message());

            if (mShowResponse)
            {
                ResponseBody body = clone.body();
                if (body != null)
                {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null)
                    {
                        Log.e(mTag, "responseBody's contentType : " + mediaType.toString());
                        if (isText(mediaType))
                        {
                            String resp = body.string();
                            Log.e(mTag, "responseBody's content : " + resp);

                            body = ResponseBody.create(mediaType, resp);
                            return response.newBuilder().body(body).build();
                        } else
                        {
                            Log.e(mTag, "responseBody's content : " + " maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            }

            Log.e(mTag, "========response'log=======end");
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return response;
    }

    private void logForRequest(Request request)
    {
        try
        {
            String url = request.url().toString();
            Headers headers = request.headers();

            Log.e(mTag, "========request'log=======");
            Log.e(mTag, "method : " + request.method());
            Log.e(mTag, "url : " + url);
            if (headers != null && headers.size() > 0)
            {
                Log.e(mTag, "headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (requestBody != null)
            {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null)
                {
                    Log.e(mTag, "requestBody's contentType : " + mediaType.toString());
                    if (isText(mediaType))
                    {
                        Log.e(mTag, "requestBody's content : " + bodyToString(request));
                    } else
                    {
                        Log.e(mTag, "requestBody's content : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }
            Log.e(mTag, "========request'log=======end");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean isText(MediaType mediaType)
    {
        if (mediaType.type() != null && mediaType.type().equals("text"))
        {
            return true;
        }
        if (mediaType.subtype() != null)
        {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request)
    {
        try
        {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e)
        {   e.printStackTrace();
            return "something error when show requestBody.";
        }
    }
}
