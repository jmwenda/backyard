package com.backyard.backyard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.backyard.backyard.PreferenceListFragment.OnPreferenceAttachedListener;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteStatement;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.BaseColumns;

public class Backyardhome extends Activity {
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
	//database 
	ReportDataSQLHelper reportdata;
	private String[] allColumns = { ReportDataSQLHelper.SECTOR, ReportDataSQLHelper.ISSUE,ReportDataSQLHelper.DESC,ReportDataSQLHelper.LATITUDE,ReportDataSQLHelper.LONGITUDE,ReportDataSQLHelper.PHOTO , ReportDataSQLHelper.VIDEO , ReportDataSQLHelper.TIME};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
        startActivity(new Intent(this, ShowSettingsActivity.class));
        return true;
        default:
        return super.onOptionsItemSelected(item);
        }
    }
    public void newreport(View v)
    {
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    	
    }
    public void viewreports(View v)
    {
    	Intent intent = new Intent(this, ViewReports.class);
    	startActivity(intent);
    	
    }
    public void help(View v)
    {
    	Intent intent = new Intent(this, Help.class);
    	startActivity(intent);
    	
    }
    public void Sync(View v)
    {
    	// prepare for a progress bar dialog
		progressBar = new ProgressDialog(v.getContext());
		
		progressBar.setCancelable(false);
		progressBar.setMessage("Sourcemap Syncing ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.show();
		//reset progress bar status
		progressBarStatus = 0;
		new Thread(new Runnable() {
			  public void run() {
				while (progressBarStatus < 100) {
				
				  // process some tasks
				  progressBarStatus = doSync();
				  
				  // your computer is too fast, sleep 1 second
				  try {
					Thread.sleep(1000);
				  } catch (InterruptedException e) {
					e.printStackTrace();
				  }

				  // Update the progress bar
				  progressBarHandler.post(new Runnable() {
					public void run() {
					  progressBar.setProgress(progressBarStatus);
					}
				  });
				}

				// ok, file is downloaded,
				if (progressBarStatus >= 100) {

					// sleep 2 seconds, so that you can see the 100%
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// close the progress bar dialog
					progressBar.dismiss();
				}
				// ok, kill thread,
				if (progressBarStatus == 1000) {
					Thread.currentThread().interrupt();
					// close the progress bar dialog
					progressBar.dismiss();				
		    		 
				}
			  }
		       }).start();
    }
    public void export()
    {
    	File f=new File("/data/data/com.backyard.backyard/databases/backyard.db");
    	FileInputStream fis=null;
    	FileOutputStream fos=null;

    	try
    	{
    	  fis=new FileInputStream(f);
    	  fos=new FileOutputStream("/mnt/sdcard/Pictures/Backyard/db_dump.db");
    	  while(true)
    	  {
    	    int i=fis.read();
    	    if(i!=-1)
    	    {fos.write(i);}
    	    else
    	    {break;}
    	  }
    	  fos.flush();
    	  //Toast.makeText(this, "DB dump OK", Toast.LENGTH_LONG).show();
    	}
    	catch(Exception e)
    	{
    	  e.printStackTrace();
    	  //Toast.makeText(this, "DB dump ERROR", Toast.LENGTH_LONG).show();
    	}
    	finally
    	{
    	  try
    	  {
    	    fos.close();
    	    fis.close();
    	  }
    	  catch(IOException ioe)
    	  {}
    	}
    }
    
    public int doSync()
    {
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    	String username = sharedPref.getString("sourcemapusername",null);
    	String sourcemappass = sharedPref.getString("sourcemappassword",null);
    	username = "afri";
    	sourcemappass = "testthis";
    	int percentage = 0;
    	if ((username != null) && (sourcemappass != null)){
    	export();
    	//we get records from the database
        Log.d("fetch: ", "Syncing ..");
        String password = "foo123";
        reportdata = new ReportDataSQLHelper(this);
        SQLiteDatabase.loadLibs(this);
        SQLiteDatabase db = reportdata.getWritableDatabase(password);
    	long totalrecords = fetchrecords(db);
    	Log.d("records: ", " "+ totalrecords +"");
    	Integer records = (int) (long) totalrecords;
    	String selectQuery = "SELECT  * FROM " + ReportDataSQLHelper.TABLE + " WHERE SYNC = '0'";
    	//Cursor cursor = db.query(ReportDataSQLHelper.TABLE,allColumns, null, null, null, null, null);
    	Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int record = 1;

    	if (records == 0)
    	{
    		percentage = 101;
    	}
        while (!cursor.isAfterLast() && records != 0) {
          Report report = cursorToReport(cursor);
          
          
          //we now push the report to sourcemap
          sourcemappush(report,username,sourcemappass);
          updateReport(report);
          record = record + 1;
          percentage = (int) (record/totalrecords) * 100;
          cursor.moveToNext();
          //Log.d("percent:",""+percentage+"");
          progressBarStatus = percentage;
          //Log.d("progress status",""+mProgressStatus+"");
        }
        // Make sure to close the cursor
        cursor.close();
    	
    	} else {
    		progressBarStatus = 1000;
    		progressBar.dismiss();
    
    		
    		 
    	}
    	return percentage;
    }
    public int updateReport(Report report) {
        String password = "foo123";
        reportdata = new ReportDataSQLHelper(this);
        SQLiteDatabase.loadLibs(this);
        SQLiteDatabase db = reportdata.getWritableDatabase(password);
     
        ContentValues values = new ContentValues();
        values.put(ReportDataSQLHelper.SYNC, "1");
        Log.d("id",""+report.getID()+"");
        // updating row
        return db.update(ReportDataSQLHelper.TABLE, values, BaseColumns._ID + " = ?",
                new String[] { String.valueOf(report.getID()) });
        
        
    }
    public void sourcemappush(Report report,String username, String pass)
    {
    	Random random = new Random();
    	HttpClient httpClient = new DefaultHttpClient();
    	String map_set = null;
    	try {
			decrypt(report._photo);
			decrypt(report._video);
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

    	HttpPost request = new HttpPost("http://beta.mysourcemap.com/member/login?format=json");
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	nameValuePairs.add(new BasicNameValuePair("username", "jmwenda"));  
    	nameValuePairs.add(new BasicNameValuePair("password", "DaYu2005"));
    	nameValuePairs.add(new BasicNameValuePair("submit", "Login"));
    	nameValuePairs.add(new BasicNameValuePair("api_key", random.toString()));
    	HttpParams params = new BasicHttpParams();
    	params.setParameter("username", username);
    	params.setParameter("password", pass);
    	params.setParameter("api_key", random.toString());
    	params.setParameter("submit", "Login");
    	
    	request.setParams(params);
    	
    	if (report._sector.equals("Forestry"))
    	{
    		map_set = "50f264a03988cc163c00000e";
    	}
    	if (report._sector.equals("Mining"))
    	{
    		map_set = "50f2648b3988cc3d4000000f";
    	}
    	if (report._sector.equals("Agriculture"))
    	{
    		map_set =  "50f264283988cc533e00000c";
    		
    	}
    	if (report._sector.equals("Bush Meat"))
    	{
    		map_set = "50fa5edb3988cc1217000090";
    	}
    	
    	try {
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			request.setParams(params);
			try {
				//String responseBody = EntityUtils.toString(request.getEntity());
				//Log.d("request body", responseBody);
				HttpResponse response = httpClient.execute(request);
				HttpEntity resEntityGet;
				String objectid;
				String object;
				
				Log.d("sector", report._sector);

				
				Log.d("response: ", " "+ response.getStatusLine().getStatusCode() +"");
				
				Log.d("request user", request.getParams().getParameter("username").toString());
				Log.d("request user", request.getParams().getParameter("password").toString());
				ArrayList<NameValuePair> attpairs = new ArrayList<NameValuePair>();
				attpairs.add(new BasicNameValuePair("issue", "test issue")); 
				HttpPost requestpost = new HttpPost("http://beta.mysourcemap.com/thing/update/null?format=json");
				ArrayList<NameValuePair> postValuePairs = new ArrayList<NameValuePair>();
				postValuePairs.add(new BasicNameValuePair("name", report._sector)); 
		    	postValuePairs.add(new BasicNameValuePair("description",report._desc));
		    	postValuePairs.add(new BasicNameValuePair("address",report._latitude +", " + report._longitude ));
		    	postValuePairs.add(new BasicNameValuePair("type", "site"));
		    	postValuePairs.add(new BasicNameValuePair("attributes[issue]", report._issue));
		    	postValuePairs.add(new BasicNameValuePair("attributes[subsector]", report._subsector));
		    	postValuePairs.add(new BasicNameValuePair("set",map_set));
		    	Log.d("postvalues",""+postValuePairs+"");
		    	requestpost.setEntity(new UrlEncodedFormEntity(postValuePairs));
		    	HttpResponse responsepost = httpClient.execute(requestpost);
		    	//resEntityGet = responsepost.getEntity();
		    	object = EntityUtils.toString(responsepost.getEntity());
		    	try {
		    		JSONObject jObject = new JSONObject(object);
		    		JSONObject object_id = jObject.getJSONObject("_id");
					objectid = object_id.getString("$id");
					
					Log.d("report photo",report._photo);
					File videofile = new File(report._video);
					//FileBody photobin = new FileBody(p);
					
					//pushfiles(httpClient,report._video,"video",objectid);
					//pushfiles(httpClient,report._photo,"photo",objectid);

					
					
					HttpContext httpContext = new BasicHttpContext();
					HttpPost httpPost = new HttpPost("http://beta.mysourcemap.com/file/accept_upload?format=json");
		            MultipartEntity entity = new MultipartEntity();
		            if (report._photo != null){
		            entity.addPart("type", new StringBody("photo"));
		            ContentBody cbFile = new FileBody(new File(report._photo), "image/jpeg");
		            //entity.addPart("file", new FileBody(videofile));
		            entity.addPart("uploaded_file", new FileBody(new File(report._photo)));
		            entity.addPart("related_id", new StringBody(objectid));
		            //entity.addPart("related_to", new StringBody("thing"));
		            entity.addPart("type", new StringBody("photo"));
		            entity.addPart("userfile", cbFile);

		            }
		            //entity.addPart("video[file]", new FileBody(new File(path)));
		            httpPost.setEntity(entity);
		            HttpResponse responsefile = httpClient.execute(httpPost);
		            
		            HttpEntity resEntity = responsefile.getEntity();  
		            Log.d("response file",""+EntityUtils.toString(resEntity)+"");
		            MultipartEntity entitytwo = new MultipartEntity();
                    if (report._video != null){
		            entitytwo.addPart("type", new StringBody("video"));
		            ContentBody cbFilevid = new FileBody(new File(report._video), "mp4");
		            //entity.addPart("file", new FileBody(videofile));
		            entitytwo.addPart("uploaded_file", new FileBody(new File(report._video)));
		            entitytwo.addPart("related_id", new StringBody(objectid));
		            //entity.addPart("related_to", new StringBody("thing"));
		            entitytwo.addPart("type", new StringBody("video"));
		            entitytwo.addPart("userfile", cbFilevid);
		    		

                    }
		    		
		    		
		            HttpPost httpPostvid = new HttpPost("http://beta.mysourcemap.com/file/accept_upload?format=json");
		            httpPostvid.setEntity(entitytwo);
		            HttpResponse responsefilevid = httpClient.execute(httpPostvid);
		            
		            HttpEntity resEntityvid = responsefilevid.getEntity();  
		            Log.d("response file",""+EntityUtils.toString(resEntityvid)+"");
		            //Toast.makeText(this, "Sync successful",Toast.LENGTH_LONG).show();
		    		try {
						encrypt(report._photo);
						encrypt(report._video);
						encrypt(report._audio);
					} catch (InvalidKeyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchPaddingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	//Log.d("request user", request.getParams().getParameter("api").toString());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    static void decrypt(String filepath) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream(filepath);
 
        FileOutputStream fos = new FileOutputStream(filepath+"decrypt");
        SecretKeySpec sks = new SecretKeySpec("enteryourkeyhere".getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    	File from = new File(filepath);
    	Log.d("from file",from.getAbsolutePath().toString());
    	File to = new File(filepath+"decrypt");
    	Log.d("to file",to.getAbsolutePath().toString());
    	to.renameTo(from);
    }
    static void encrypt(String filepath) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
    	// Here you read the cleartext.
    	FileInputStream fis = new FileInputStream(filepath);
    	// This stream write the encrypted text. This stream will be wrapped by another stream.
    	FileOutputStream fos = new FileOutputStream(filepath+"crypt");
    	// Length is 16 byte
    	SecretKeySpec sks = new SecretKeySpec("enteryourkeyhere".getBytes(), "AES");
    	// Create cipher
    	Cipher cipher = Cipher.getInstance("AES");
    	cipher.init(Cipher.ENCRYPT_MODE, sks);
    	// Wrap the output stream
    	CipherOutputStream cos = new CipherOutputStream(fos, cipher);
    	// Write bytes
    	int b;
    	byte[] d = new byte[8];
    	while((b = fis.read(d)) != -1)
    	{
    	   cos.write(d, 0, b);
    	}
    	// Flush and close streams.
    	cos.flush();
    	cos.close();
    	fis.close();
    	File from = new File(filepath);
    	File to = new File(filepath+"crypt");
    	to.renameTo(from);
    	
    	}
    public void pushfiles(HttpClient httpClient,String path, String type,String object)
    {
    	//HttpContext httpContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost("http://beta.mysourcemap.com/file/accept_upload?format=json");
		try 
        {
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("type", new StringBody("video"));
            ContentBody cbFile = new FileBody(new File(path), "image/jpeg");
            //entity.addPart("file", new FileBody(videofile));
            //entity.addPart("uploaded_file", new FileBody(new File(path)));
            //entity.addPart("related_id", new StringBody(object));
            //entity.addPart("related_to", new StringBody("thing"));
            //entity.addPart("type", new StringBody(type));
            
            entity.addPart("userfile", cbFile);
            //entity.addPart("video[file]", new FileBody(new File(path)));
            httpPost.setEntity(entity);
            HttpResponse responsefile = httpClient.execute(httpPost);
            
            HttpEntity resEntity = responsefile.getEntity();  
            Log.d("response file",""+EntityUtils.toString(resEntity)+"");
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    public long fetchrecords(SQLiteDatabase db){
    	String sql = "SELECT COUNT(*) FROM reports where SYNC = '0'";
    	SQLiteStatement statement = db.compileStatement(sql);
    	long count = statement.simpleQueryForLong();
    	return count;
    	
    }
    private Report cursorToReport(Cursor cursor) {
        Report report = new Report();
        
        report.setId(cursor.getInt(0));
        report.setSector(cursor.getString(2));
        report.setDesc(cursor.getString(5));
        report.setIssue(cursor.getString(3));
        report.setLat(cursor.getString(6));
        report.setLon(cursor.getString(7));
        report.setPhoto(cursor.getString(8));
        report.setVideo(cursor.getString(9));
        return report;
      }
}