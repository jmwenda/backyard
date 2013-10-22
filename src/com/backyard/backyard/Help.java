package com.backyard.backyard;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class Help extends ListActivity {
	public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    String[] values = new String[] { "Instructions", "FAQ", "Reference Maps"};
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	        android.R.layout.simple_list_item_1, values);
	    setListAdapter(adapter);
	  }
    @Override
    public void onBackPressed(){
     	Intent intent = new Intent(this, Backyardhome.class);
    	startActivity(intent);
    	}
}
