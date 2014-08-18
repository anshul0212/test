package com.social.healthometer;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.social.healthometer.adapter.CustomArrayAdapter;
import com.social.healthometer.model.TodoItem;
import com.social.utilities.ServiceHandler;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Toast;
//import android.widget.AdapterView.OnItemClickListener;






import com.microsoft.windowsazure.mobileservices.TableOperationCallback;


public class PendingListFragment extends Fragment implements OnItemClickListener  
{
	 
	 String[] member_names;
	 TypedArray profile_pics;
	 String[] mob_num;
	 String[] code;
	 SparseBooleanArray mCheckStates ;
	 private CustomArrayAdapter customArrayAdapter;
	 private MobileServiceClient mClient;
		private MobileServiceTable<TodoItem> mToDoTable;
		Boolean ready;
		ListView mylistview ;
	 String url_add_beneficiary;
	// List<CheckBoxItem> rowCheckBoxItems ;
		private ArrayList<TodoItem> listItems;
		//private ListView mylistview ;

	 @Override
	    public View onCreateView(LayoutInflater inflater, 
	              ViewGroup container, Bundle savedInstanceState) {
	        
	  View view = inflater.inflate(R.layout.fragment_pending,  container, false);
	   mylistview = (ListView) view.findViewById(R.id.list);
	//  l.setBackgroundColor(color.dark_red_color);
		url_add_beneficiary = getString(R.string.url_add_beneficiary);
		
		 
	  
	   mylistview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	  
	  listItems = new ArrayList<TodoItem>();  
	//  rowCheckBoxItems = new ArrayList<CheckBoxItem>();  
	  customArrayAdapter = new CustomArrayAdapter(getActivity().getApplicationContext(),listItems);
	
	 // mylistview.setItemsCanFocus(true);
	  mylistview.setAdapter(customArrayAdapter);
	 // ShowMessage("Success", "MobileServiceClient created ii successfull");
	  


	//  profile_pics.recycle();
	mylistview.setFocusable(false);
	 mylistview.setOnItemClickListener( this);
	 
	// Toast.makeText(getActivity().getApplicationContext(), "hjhghg",	    Toast.LENGTH_SHORT).show();

	  
	
	  
	
		Button btnObjVerified = (Button)view.findViewById(R.id.verify_button_id);
		btnObjVerified.setOnClickListener( new OnClickListener() {
            @Override
            public void onClick(View view) {
            	 SparseBooleanArray checked = mylistview.getCheckedItemPositions();
            	
            
            	 //ShowMessage("click",""+ "verify" );
            	
            	 TodoItem planet ;
            	int y = mylistview.getCount() ;
            	  for (int i = 0; i < y; i++)
            	  {
            		  
            		  
            		  
            		 // ShowMessage("i=","count-"+mylistview.getCount() ); 
            		   planet = customArrayAdapter.getItem(i);
            		  
            		   
            		//   ShowMessage("for","check-"+ planet.isChecked()+" name= "+planet.getText() );
            		 
            		
                      if ( planet.isChecked()	)
                      {
                    	  
                    	//  SaveClicked( planet.getText() , planet.getNotifyNumber(),  planet.getSex() );
                    	 
                    	 SaveClicked( planet );
                    	  listItems.remove(i);
                    	 y-- ;
                    	 i-- ;

                      } 
                     

                  }
            	  customArrayAdapter.notifyDataSetChanged(); 
                  mylistview.clearChoices(); 
                  ShowMessage("Success","Verified Successfully." );	 
                   
         
            	
            }});
		
		   
		
		
		
		refreshItemsFromTable();
	  
	  return view;
	  

	 }

	public static TodoItem ITEM_TO_EDIT = null;

	 
	
		//public void SaveClicked(String nameEditText ,String cellNoEditText, String sex )
		public void SaveClicked(TodoItem item  )
		{
			ITEM_TO_EDIT = item;
		
			PendingListFragment.ITEM_TO_EDIT.setComplete(true);
			//ShowMessage("exception", ViewDetailFragment.ITEM_TO_EDIT.toString());  
			 
			mToDoTable.update(PendingListFragment.ITEM_TO_EDIT, new TableOperationCallback<TodoItem>() {
			      public void onCompleted(TodoItem entity, Exception exception, ServiceFilterResponse response) {
			    	    
			            if (exception == null) 
			            {
			            		
			     
			            } 
			            else 
			            {
			            	ShowMessage("exception", exception.toString());  
			            	//	ShowMessage("Failed", "Cannot be Updated");
			            }
			      }
			      });
			
		}
		
		
	 @Override
	 public void onItemClick(AdapterView<?> parent, View view, int position,
	   long id) {
		
		 Toast.makeText(getActivity().getApplicationContext(), "df", Toast.LENGTH_SHORT).show();
	
	 }
	 

	    
	    private class PopulatePendingDetails extends AsyncTask<Void, Void, Void> 
	    {
	    	 ArrayList<TodoItem> arrayItem = new ArrayList<TodoItem>();
	            
	    	// ProgressDialog pDialog= new ProgressDialog(context);
	    	@Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            
	          //  createProgress();
	    
	    	}
		
	        @Override
	        protected Void doInBackground(Void... arg0) 
	        {
	            // Creating service handler class instance
	            ServiceHandler sh = new ServiceHandler();
	          
	            // Making a request to url and getting response
	            Log.d("url_add_beneficiary",url_add_beneficiary);
	            url_add_beneficiary = url_add_beneficiary+"?is_verified=0";
	            String  jsonStr = sh.makeServiceCall(url_add_beneficiary, ServiceHandler.GET) ;
	            
	            Log.d("Response: ", "> " + jsonStr);
	           
	           
	            		
	            if (jsonStr != null) {
	                try {
	                	JSONArray jArray = new JSONArray(jsonStr);
	                     
	                	  Log.d("jArray: ", "> " + jArray.toString());
	                      
	                	  Log.d("jArray.length(): ", "> " + jArray.length());
	                	for(int i=0; i<jArray.length() ; i++)
	                	{
	                		JSONObject c = jArray.getJSONObject(i);
	                		 TodoItem item = new TodoItem();
	                   
	                        String name = c.getString("name");
	                        String dob = c.getString("dob");             
	                        
	                        item.setDateOfBirth(dob.toString());
	                        item.setText(name);
	                        arrayItem.add(item);
	                        Log.d("itemgetText: ", "> " + item.getText());
	           	    	 Log.d("arrayItem: ", "> " + arrayItem);
	                    //    customArrayAdapter.add(item);
	                	}
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
		    	 Log.d("arrayItem: ", "> " + arrayItem);
		    	 Iterator<TodoItem> itr =  arrayItem.iterator();
		    	 
		    	int er =0;
		    	while(itr.hasNext())
		    	{
		    	// Log.d("arrayItem: ", "> " +);
		    	// er++;
		    		customArrayAdapter.add(itr.next());
		    	}
		    	
		    	
		    	 
		    	//
		    	//customArrayAdapter.notifyDataSetChanged();	  
		    	/*
		    	EditText etName = (EditText)getActivity().findViewById(R.id.enter_name_text);
		    	etName.setText(user.get("name"));
		    	
		    	EditText etDate = (EditText)getActivity().findViewById(R.id.date_of_birth);
		    	etDate.setText(user.get("dob"));
		    	//dismissProgress();
		    	*/
		    }    
	    }
	    
	    

	 private void refreshItemsFromTable() {

			// Get the items that weren't marked as completed and add them in the
			// adapter
	
		 PopulatePendingDetails populatePendingDetails = new PopulatePendingDetails();
		 
		 populatePendingDetails.execute();
		 /*
			mToDoTable.where().field("complete").eq(val(false)).execute(new TableQueryCallback<TodoItem>() {
				
				public void onCompleted(List<TodoItem> result, int count, Exception exception, ServiceFilterResponse response) {
					
					if (exception == null) {
						customArrayAdapter.clear();
						
						for (TodoItem item : result) {
							customArrayAdapter.add(item);
						}

						customArrayAdapter.notifyDataSetChanged();	  
					} else {
						ShowMessage("exception", exception.toString());
					}
				}
			});
			
			*/
		}

		public void ShowMessage(String title, String message)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

			builder.setMessage(message);
			
			builder.setIcon(R.drawable.ic_action_done);
			builder.setTitle(title);
			builder.create().show();
		}
		ImageView seeDetailImg;
		
		
		 @Override
		 public void onActivityCreated(Bundle savedInstanceState) {
		     // TODO Auto-generated method stub
		     super.onActivityCreated(savedInstanceState);
	
		 	final Button btnMenu = (Button) getView().findViewById(R.id.menu_button_id);
			
			//final View v = view;
				btnMenu.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						
						//Toast.makeText(getApplicationContext(), "add", Toast.LENGTH_LONG).show();
						Intent i = new Intent(v.getContext(), MainMenuActivity.class);
						startActivity(i);
					}
				});	
		 }
	
	} 


