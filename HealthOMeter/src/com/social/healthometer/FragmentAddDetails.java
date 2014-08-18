package com.social.healthometer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.social.actionlisteners.ButtonClickListener;
import com.social.healthometer.model.TodoItem;
import com.social.utilities.ServiceHandler;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

public class FragmentAddDetails extends Fragment implements OnDateSetListener,OnTouchListener,OnClickListener  {
	 private ProgressBar spinner;
	 
	private MobileServiceClient mClient;
	private Boolean ready;
	RadioButton maleRadioButton;
	RadioButton femaleRadioButton;
	Button submitButton;
	Button resetButton;
	EditText nameEditText;
	EditText cellNoEditText;
	DatePicker DOBDatePicker;
	private Button searchButton;
	private EditText searchEditText;
	private ListView listView;
	//private DatePicker datePicker;
	private EditText dateOfbirth;
	private MobileServiceTable<TodoItem> mToDoTable;
	private TodoItem item;
    private int mYear, mMonth, mDay,mHour,mMinute; 
    
    String  url_add_beneficiary;
    private Context context;
	private ProgressDialog pd;
	//private Button b;
	Button menuButton;
    ButtonClickListener onClickListener ;
    
	
	public TodoItem getItem() {
		return item;
	}

	public void setItem(TodoItem item) {
		this.item = item;
	}

	public View onCreateView(  LayoutInflater inflater, ViewGroup container , Bundle bundle)
	{
		context = this.getActivity();
		View rootView = inflater.inflate(R.layout.fragment_add_edit_details, container, false);
		
		url_add_beneficiary = getString(R.string.url_add_beneficiary);
		
		 
		
		return rootView;
	}
	
    
    /////////
    
    
    private class GetProfileDetails extends AsyncTask<Void, Void, Void> 
    {
    	HashMap<String, String> user;
    	String name, dob;
    	// ProgressDialog pDialog= new ProgressDialog(context);
    	@Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            createProgress();
            
            
    	}
   	 
    	private StringBuilder inputStreamToString(InputStream is) {
    		String line = "";
    		StringBuilder total = new StringBuilder();
    		  // Wrap a BufferedReader around the InputStream
    		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    		// Read response until the end
    		try {
    			while ((line = rd.readLine()) != null) { 
    				total.append(line); 
    				}
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    	// Return full string
    	return total;
    	
    	
    	}

    	
        @Override
        protected Void doInBackground(Void... arg0) 
        {
        	
      
        	EditText etName = (EditText)getActivity().findViewById(R.id.enter_name_text);
	    	name = etName.getText().toString();
	    	
	    	EditText etDate = (EditText)getActivity().findViewById(R.id.date_of_birth_text);
	    	dob = etDate.getText().toString();
	   
	    	if(dob!=null)
	    	{
	    	String dobaArray[] = new String[3];
	    	
	    	dobaArray = dob.split("-");
	    	
	    	dob = "";
	    	dob = dob + dobaArray[2] +"/"+ dobaArray[1] +"/"+ dobaArray[0] ;
	    	}
	    	else
	    		 Log.d("dob","dob is null");
	    		
        	
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            Log.d("dob",dob);
            
            // Making a request to url and getting response
            Log.d("url_add_beneficiary",url_add_beneficiary);
            
        
         // Building post parameters, key and value pair
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
            //nameValuePair.add(new BasicNameValuePair("Id", "1"));
            nameValuePair.add(new BasicNameValuePair("name", name));
           nameValuePair.add(new BasicNameValuePair("notify_num", "232323"));
            nameValuePair.add(new BasicNameValuePair("dob", dob));
      		
            String  jsonStr = sh.makeServiceCall(url_add_beneficiary, ServiceHandler.POST, nameValuePair) ;
            
            Log.d("Response: ", "> " + jsonStr);
 
            if (jsonStr != null) {
                try {
                	JSONObject c = new JSONObject(jsonStr);
                     
                   
                        String name = c.getString("name");
                        String dob = c.getString("dob");             
                        user = new HashMap<String, String>();
                        // adding each child node to HashMap key => value
                        user.put("name", name);
                        user.put("dob", dob);
                     
                       Log.d("user: ", "> " + user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }  
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            
            
            return null;    
        }
	     
	    @Override
	    protected void onPostExecute(Void result) {
	    
	    	super.onPostExecute(result);
	    	
	    	EditText etName = (EditText)getActivity().findViewById(R.id.enter_name_text);
	    	etName.setText(user.get("name"));
	    	
	    	EditText etDate = (EditText)getActivity().findViewById(R.id.date_of_birth_text);
	    	etDate.setText(user.get("dob"));
	    	dismissProgress();
	    	
	    }    
    }
    
    ////////////
    
    
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

         onClickListener=new ButtonClickListener(this.getActivity());
         menuButton = (Button)getActivity().findViewById(R.id.menuadd_button_id);
         menuButton.setOnClickListener(onClickListener);
		
		
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
  
        
        dateOfbirth=(EditText)getActivity().findViewById(R.id.date_of_birth_text);
        maleRadioButton = (RadioButton)getActivity().findViewById(R.id.male_radio);
        femaleRadioButton = (RadioButton)getActivity().findViewById(R.id.female_radio);
        nameEditText = (EditText)getActivity().findViewById(R.id.enter_name_text);
    	cellNoEditText = (EditText)getActivity().findViewById(R.id.phone_num_text);
    	submitButton = (Button)getActivity().findViewById(R.id.button1);
    	
    	InputMethodManager imm=(InputMethodManager)this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromInputMethod(dateOfbirth.getWindowToken(),0);
        dateOfbirth.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL);
    	  dateOfbirth.setOnClickListener(this);
    	  maleRadioButton.setOnClickListener(
    		        new RadioButton.OnClickListener() {
    		        	public void onClick(View v) {
    		                	  // Code to be performed when 
    					  // the button is clicked
    		        		MaleSelected(v);
    		        		}
    		        	}
    		        );
    	  
    		
    		femaleRadioButton.setOnClickListener(
    			        new RadioButton.OnClickListener() {
    			        	public void onClick(View v) {
    			                	  // Code to be performed when 
    						  // the button is clicked
    			        		FemaleSelected(v);
    			        		}
    			        	}
    			        );
   
    		submitButton.setOnClickListener(
    		        new Button.OnClickListener() {
    		        	public void onClick(View v) {
    		                	  // Code to be performed when 
    					  // the button is clicked
    		        		 SubmitClicked(v);
    		        		}
    		        	}
    		        );
    	
    	
        
    /*
        {
    	mClient=new MobileServiceClient("https://vaccineproapp.azure-mobile.net/",  "AKuNBrSjWykyVFDZFWmwECbDlyfjvt98", this.getActivity().getBaseContext());
		
	mToDoTable = mClient.getTable(TodoItem.class);
	if(mToDoTable != null)
	{
		ready = true;
		
	}
		
        
        
        }catch(Exception e)
        {}
    	
*/
		if (item!=null) {
			
		
			nameEditText.setText(item.getText());
		
			
			if (item.getSex().compareToIgnoreCase("male")==0) {
				maleRadioButton.setChecked(true);
				femaleRadioButton.setChecked(false);
			}
			else
			{
				femaleRadioButton.setChecked(true);
				maleRadioButton.setChecked(false);
				
			}
			
			dateOfbirth.setText(item.getDateOfBirth().toString());
			cellNoEditText.setText(item.getMobileNumber());
			
			nameEditText.setEnabled(false);
			maleRadioButton.setEnabled(false);
			femaleRadioButton.setEnabled(false);
			dateOfbirth.setEnabled(false);
			cellNoEditText.setEnabled(false);
			submitButton.setText("Update?");
			
		}	
        
        
	}
	
	 
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		
    monthOfYear=monthOfYear+1;		
	dateOfbirth.setText(dayOfMonth+"-"+monthOfYear+"-"+year);
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		DatePickerDialog datePicker=  new DatePickerDialog(this.getActivity(), this, mYear, mMonth, mDay);
		   datePicker.show();
		return true;
	}
	
	public void MaleSelected(View view)
	{
		maleRadioButton.setChecked(true);
		femaleRadioButton.setChecked(false);
	}

	public void FemaleSelected(View view)
	{
		femaleRadioButton.setChecked(true);
		maleRadioButton.setChecked(false);
	}

	public void ResetClicked(View view)
	{
		nameEditText.setText("");
		cellNoEditText.setText("");
		femaleRadioButton.setChecked(false);
		maleRadioButton.setChecked(false);	
		
	}

	public void ShowMessage(String title, String message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}
	public void createProgress()
	{
		String Processing = getResources().getString(R.string.Processing);
		String pleaseWait = getResources().getString(R.string.PleaseWait);
		
	pd = new ProgressDialog(context);
	pd.setTitle(Processing);
	pd.setMessage(pleaseWait);
	pd.setCancelable(false);
	pd.setIndeterminate(true);
	pd.show();
	}
	
	public void dismissProgress()
	{
		
		if (pd!=null) {
			pd.dismiss();
			submitButton.setEnabled(true);
		}
	}
	
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null &&
	    		activeNetworkInfo.isConnectedOrConnecting();
	   
	}
	
	
	public void SubmitClicked(View view)
	{
		GetProfileDetails addBenificiary = new GetProfileDetails();
		addBenificiary.execute();
		
		
	/*	
		view.setEnabled(false);
		AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {
		RadioButton maleRadioButton;
		RadioButton femaleRadioButton;
		EditText dateOfbirth;
		EditText nameEditText, cellNoEditText ;
		Button submitButton;
		
		String buttonDelete = getResources().getString(R.string.Delete);
		String buttonSave = getResources().getString(R.string.Save);
		String buttonSubmit = getResources().getString(R.string.submit);
		
		@Override
		protected void onPreExecute() {
		    dateOfbirth=(EditText)getActivity().findViewById(R.id.date_of_birth_text);
	        maleRadioButton = (RadioButton)getActivity().findViewById(R.id.male_radio);
	        femaleRadioButton = (RadioButton)getActivity().findViewById(R.id.female_radio);
	        nameEditText = (EditText)getActivity().findViewById(R.id.enter_name_text);
	    	cellNoEditText = (EditText)getActivity().findViewById(R.id.phone_num_text);
	    	submitButton = (Button)getActivity().findViewById(R.id.button1);
	    	//String a = getActivity().getApplicationContext().
	    	createProgress();	
			
		}
			
		@Override
		protected Void doInBackground(Void... arg0) {
			
		try {
			 //ShowMessage("Insert","Inserting");  
			Log.e("start", "msg="+isNetworkAvailable());
			if (submitButton.getText().equals("Update?")) 
			{			
				nameEditText.setEnabled(true);
				maleRadioButton.setEnabled(true);
				femaleRadioButton.setEnabled(true);
				dateOfbirth.setEnabled(true);
				cellNoEditText.setEnabled(true);
			}// if update
			
			if (submitButton.getText().equals("Save"))
			{
				item.setText(nameEditText.getText().toString());
				item.setMobileNumber(cellNoEditText.getText().toString());
				if(this.maleRadioButton.isChecked())
				{
					item.setSex("male");
				}
				else
				{
					item.setSex("female");
				}
				try
				{
					
					
					mToDoTable.update(item, new TableOperationCallback<TodoItem>() 
					{
						public void onCompleted(TodoItem entity, Exception exception, ServiceFilterResponse response) 
						{
						    	    
							if (exception == null) 
							{
								dismissProgress();
								//ShowMessage("Success","Records Updated");          		     
							} 
							else 
							{
								dismissProgress();
				       
							//ShowMessage("Failed", "Cannot be Updated");
							}
						}
					});
						
				}catch(Exception e)
				{
					dismissProgress();
				//	ShowMessage("Exception", e.toString());  
							
				}
						
						
			}// if save
		
			if (submitButton.getText().equals("Update?")) 
			{
				submitButton.setText("Save");
			}			
							
			if (submitButton.getText().equals(buttonSubmit)) 
			{
				Log.d("submit", "msg");		
				if((nameEditText.getText().length() == 0) || (cellNoEditText.getText().length() == 0) ||
							((!maleRadioButton.isChecked()) && (!femaleRadioButton.isChecked())))
				{
					dismissProgress();
		        	
					publishProgress(3);
					Log.d("insert", "msg");
				}
				else
				{
					Log.d("else", "msg");
					//process here
					TodoItem item = new TodoItem();
					item.setText(nameEditText.getText().toString());
					item.setRegCode(cellNoEditText.getText().toString());
					if(this.maleRadioButton.isChecked())
					{
						item.setSex("male");
					}
					else
					{
						item.setSex("female");
					}
					
					Log.d("inserttt", "msg");	
					mToDoTable.insert(item, new TableOperationCallback<TodoItem>() 
					{
						public void onCompleted(TodoItem entity, Exception exception, ServiceFilterResponse response) 
						{	 
					        if (exception == null) 
					        {           	
					            dismissProgress();
					        	nameEditText.setText("");
					          	cellNoEditText.setText("");
					          	dateOfbirth.setText("");
					          	femaleRadioButton.setChecked(false);
					          	maleRadioButton.setChecked(false);
					          	 publishProgress(1);
					         }
					         else 
					         {
					        	 
					        	 dismissProgress();
					        	 publishProgress(4);
					            }
					        	 
					            	
					            	
					      }
					});		
					
					
				}// else
			
			}// if submit
				
				
			} catch (Exception e) {
				
				dismissProgress();
				// TODO Auto-generated catch block
				publishProgress(2);
			e.printStackTrace();
			Log.d("exc", e.toString() );
			//ShowMessage("Failed", "Can not be Added");
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			
		}
		
		 @Override
		 protected void onProgressUpdate(Integer... progress) {
			 
				String Success = getResources().getString(R.string.Success);
				
				String AddSuccess = getResources().getString(R.string.AddedSuccess);
				
				String Fail = getResources().getString(R.string.Failed);
				
				String CannotAdd = getResources().getString(R.string.Cannotbeadded);
				
	String Warning = getResources().getString(R.string.Warning);
				
				String FullDetail = getResources().getString(R.string.FullDetail);
				String NoNetwork = getResources().getString(R.string.NoNetwork);
					
			 if(progress[0] == 1)
			 {
					ShowMessage(Success, AddSuccess);
			 }
			 if(progress[0] == 2)
			 {
			 ShowMessage(Fail, CannotAdd);
			 }
			 if(progress[0] == 3)
			 {
			 ShowMessage(Warning, FullDetail);
			 }
			 if(progress[0] == 4)
			 {
			 ShowMessage(Warning, NoNetwork);
			 }
		 }
			
	};
	task.execute((Void[])null);
		
		*/
	
	}
	
	
	@Override
	public void onClick(View v) {
		 DatePickerDialog datePicker=  new DatePickerDialog(this.getActivity(), this, mYear, mMonth, mDay);
		   datePicker.show();
		
		
	}

	
	
}
