package de.dailab.apppets.plib.pservices.storage;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import apppets.plib.R;
import de.dailab.apppets.plib.keyGenerator.keystore.TrustStoreHandler;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

/**
 * Created by arik on 18.09.2017.
 */

public class PServiceKeyValueStorageRemoteRestPython extends PServiceKeyValueStorageAbstract {
    
    private static final int MAX_NUMBER = 30;
    private static final String WEB_CONTEXT_PATH = "/storage/v1";
    private static Object sync = new Object();
    private static PServiceKeyValueStorageRemoteRestPython me = null;
    
    
    private static HashMap<Integer, JSONObject> db = null;
    
    private PServiceKeyValueStorageRemoteRestPython() {
    }
    
    
    protected static PServiceKeyValueStorageRemoteRestPython getInstance() {
        if (me == null) {
            me = new PServiceKeyValueStorageRemoteRestPython();
        }
        return me;
    }
    
    public void test(Context context) {
        
        String appContext = "TODOLIST";
        boolean b1 = true;
        boolean b2 = true;
        boolean b3 = true;
        boolean b4 = true;
        boolean b5 = true;
        boolean b6 = true;
        boolean b7 = true;
        boolean b8 = true;
        
        String key1 = "theKey1";
        String eKey1a = encryptKey(context, key1, appContext);
        String eKey1b = encryptKey(context, key1, appContext);
        b1 = eKey1a.equals(eKey1b);
        
        
        String key2 = "theKey2";
        String eKey2a = encryptKey(context, key2, appContext);
        String eKey2b = encryptKey(context, key2, appContext);
        b2 = eKey2a.equals(eKey2b);
        
        b3 = !eKey1a.equals(eKey2a);
        
        JSONObject json1 = new JSONObject();
        try {
            json1.put("name", "theValue1");
        } catch (JSONException e) {
        
        
        }
        try {
            json1.put("ref", key1);
        } catch (JSONException e) {
        
        
        }
        try {
            json1.put("state", true);
        } catch (JSONException e) {
        
        
        }
        try {
            json1.put("id", 123);
        } catch (JSONException e) {
        }

    }
    
    
    @Override
    public PServiceKeyValueResult get(Context context, String key, String appContext) {
        synchronized (sync) {
            PServiceKeyValueResult result = new PServiceKeyValueResult();
            result.setMsg(context.getString(R.string.theplib_undefined));
            result.setKey(key);
            byte[] value = null;
            try {
                OkHttpClient client = getRestClient(context);
                if (client == null) {
                    result.setResultType(PServiceKeyValueResult.PServiceKeyValueResultType.ERROR);
                    result.setMsg(context.getString(R.string.theplib_undef_err));
                    return result;
                }
                String url = getOkHttpBaseUrl(context) + "/" + encryptKey(context, key, appContext);
                
                Request request = new Request.Builder()
                                          .url(url).get()
                                          .build();
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    value = response.body().bytes();
                } else {
                    result.setMsg(context.getString(R.string.theplib_resp_code) + " "+ response.code());
                    result.setResultType(
                            PServiceKeyValueResult.PServiceKeyValueResultType.NOT_FOUND);
                    return result;
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                result.setMsg(e.getLocalizedMessage());
                result.setResultType(PServiceKeyValueResult.PServiceKeyValueResultType.ERROR);
                return result;
            }
            
            try {
                byte[] decryptedRaw = decryptValue(context, value, appContext);
                if(decryptedRaw==null){
                    throw new Exception(context.getString(R.string.theplib_enc_dec_error));
                }
                result.setData(decryptedRaw);
                result.setResultType(PServiceKeyValueResult.PServiceKeyValueResultType.NO_ERROR);
                return result;
            } catch (Exception e) {
                result.setMsg(e.getLocalizedMessage());
                result.setResultType(
                        PServiceKeyValueResult.PServiceKeyValueResultType.ENCRYPTION_ERROR);
                return result;
            }
        }
    }
    
    private boolean equalContent(JSONObject o1, JSONObject o2) {
        if (o1 == null && o2 == null) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.toString().equals(o2.toString());
        
    }    private OkHttpClient getRestClient(Context context) {
        
        try {
            long to = PServiceHandlerKeyValueStorage.getTimout(context);
            
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                                                   .connectTimeout(to, TimeUnit.MILLISECONDS)
                                                   .writeTimeout(to, TimeUnit.MILLISECONDS)
                                                   .readTimeout(to, TimeUnit.MILLISECONDS);
            OkHttpClient client = null;
            
            if (PServiceHandlerKeyValueStorage.useTls(context)) {
                ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                                              .tlsVersions(TlsVersion.TLS_1_3)
                                              .allEnabledCipherSuites().allEnabledTlsVersions()
                                              .build();
                builder.connectionSpecs(Collections.singletonList(spec));
                KeyStore trustStore = TrustStoreHandler.getPlibTrustStore(context);
                TrustManagerFactory tmf = null;
                SSLContext ctx = null;
                
                try {
                    tmf = TrustManagerFactory
                                  .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(trustStore);
                    ctx = SSLContext.getInstance("TLSv1");
                    ctx.init(null, tmf.getTrustManagers(), null);
                    SSLSocketFactory factory = ctx.getSocketFactory();
                    builder.sslSocketFactory(factory);
                    if (!PServiceHandlerKeyValueStorage.useHostnameVerifier(context)) {
                        builder.hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        });
                    }
                } catch (Exception e) {
                    // exception handling
                    e.printStackTrace();
                }
                
                
                client = builder.build();
                
            } else {
                client = builder.build();
            }
            return client;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String getOkHttpBaseUrl(Context context) {
        String res = "http" + (PServiceHandlerKeyValueStorage.useTls(context) ? "s" : "") + "://" +
                             PServiceHandlerKeyValueStorage.getAddress(context) + ":" +
                             PServiceHandlerKeyValueStorage.getPort(context) + WEB_CONTEXT_PATH;
        return res;
    }
    
    @Override
    public PServiceKeyValueResult put(Context context, String key, byte[] value, String appContext) {
        PServiceKeyValueResult result= new PServiceKeyValueResult();
        result.setMsg(context.getString(R.string.theplib_undef_err));
        result.setKey(key);
        synchronized (sync) {
            try {
                OkHttpClient client = getRestClient(context);
                if (client == null) {
                    result.setResultType(PServiceKeyValueResult.PServiceKeyValueResultType.ERROR);
                    result.setMsg(context.getString(R.string.theplib_undef_err));
                    return result;
                }
                String url = getOkHttpBaseUrl(context) + "/" + encryptKey(context, key, appContext);
                
                byte[] enc = encryptValue(context, value, appContext);
                Request request = new Request.Builder()
                                          .url(url).post(RequestBody
                                                                 .create(MediaType
                                                                                 .parse("application/octet-stream"),
                                                                         enc)).build();
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    result.setResultType(PServiceKeyValueResult.PServiceKeyValueResultType.NO_ERROR);
                    return result;
                } else {
                    result.setMsg(context.getString(R.string.theplib_resp_code) + " "+ response.code());
                    result.setResultType(
                            PServiceKeyValueResult.PServiceKeyValueResultType.ERROR);
                    return result;
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                result.setMsg(e.getLocalizedMessage());
                result.setResultType(PServiceKeyValueResult.PServiceKeyValueResultType.ERROR);
                return result;
            }
            
            
        }
    }
    
    @Override
    public Boolean delete(Context context, String key, String appContext) {
        synchronized (sync) {
            
            String value;
            try {
                OkHttpClient client = getRestClient(context);
                if (client == null) {
                    return null;
                }
                
                String url = getOkHttpBaseUrl(context) + "/" + encryptKey(context, key, appContext);
                
                Request request = new Request.Builder()
                                          .url(url).delete()
                                          .build();
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    value="true";
                } else {
                    return null;
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
                return false;
            }
            
        }
    }
    
    @Override
    public PServiceKeyValueMaxResult getMaximumEntries(Context context, String appContext) {
        PServiceKeyValueMaxResult resMax= new PServiceKeyValueMaxResult();
        try {
            PServiceKeyValueResult res = get(context, "task_max", appContext);
    
            if (res.getResultType() == PServiceKeyValueResult.PServiceKeyValueResultType.UNDEFINED
                        || res.getResultType() ==
                                   PServiceKeyValueResult.PServiceKeyValueResultType.ERROR
                    ) {
                resMax.setMsg(res.getMsg());
                resMax.setResultType(PServiceKeyValueMaxResult.PServiceKeyValueMaxResultType.ERROR);
                return resMax;
            }
            if(res.getResultType()== PServiceKeyValueResult.PServiceKeyValueResultType.NOT_FOUND || res.getResultType()==
                                                                                                            PServiceKeyValueResult.PServiceKeyValueResultType.ENCRYPTION_ERROR){
                int max= 0;
                PServiceKeyValueResult mRes= setNewTaskMax(context, appContext, max);
                if(mRes.getResultType()== PServiceKeyValueResult.PServiceKeyValueResultType.NO_ERROR) {
                    resMax.setResultType(
                            PServiceKeyValueMaxResult.PServiceKeyValueMaxResultType.NO_ERROR);
                    resMax.setMax(max);
                }
                else{
                    resMax.setResultType(
                            PServiceKeyValueMaxResult.PServiceKeyValueMaxResultType.ERROR);
                    resMax.setMax(0);
                    resMax.setMsg(mRes.getMsg());
                }
                return resMax;
            }
            int max;
            byte[] data= res.getData();
            if(data==null|| data.length!=2){
                max= 0;
                PServiceKeyValueResult mRes= setNewTaskMax(context, appContext, max);
                if(res.getResultType()!= PServiceKeyValueResult.PServiceKeyValueResultType.NO_ERROR) {
                    resMax.setResultType(
                            PServiceKeyValueMaxResult.PServiceKeyValueMaxResultType.NO_ERROR);
                    resMax.setMax(max);
                }
                else{
                    resMax.setResultType(
                            PServiceKeyValueMaxResult.PServiceKeyValueMaxResultType.ERROR);
                    resMax.setMax(0);
                    resMax.setMsg(mRes.getMsg());
                }
                return resMax;
            }

            max = (short) (data[0]<<8 | data[1] & 0xFF);
            resMax.setResultType(PServiceKeyValueMaxResult.PServiceKeyValueMaxResultType.NO_ERROR);
            resMax.setMax(max);
            return resMax;
            
        } catch (Exception e) {
            e.printStackTrace();
            resMax.setMsg(e.getLocalizedMessage());
            resMax.setResultType(PServiceKeyValueMaxResult.PServiceKeyValueMaxResultType.ERROR);
            return resMax;
        }
    }
    
    private PServiceKeyValueResult setNewTaskMax(Context context, String appContext, int val) {
        
        byte[] data= new byte[2];
        short s= (short)val;
        data[0] = (byte) (s >> 8);
        data[1] = (byte) s;
        PServiceKeyValueResult res= put(context,"task_max",data, appContext);
        return res;
    }
    
    @Override
    public String getDbContent(Context context) {
        synchronized (sync) {
            
            String value;
            try {
                OkHttpClient client = getRestClient(context);
                if (client == null) {
                    return null;
                }
                
                String url = getOkHttpBaseUrl(context) + "/all";
                
                Request request = new Request.Builder()
                                          .url(url).get()
                                          .build();
                Response response = client.newCall(request).execute();
                if (response.code() == 200) {
                    value = response.body().string();
                } else {
                    return null;
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            
            return value;
            
        }
    }
    

    

    
    
}
