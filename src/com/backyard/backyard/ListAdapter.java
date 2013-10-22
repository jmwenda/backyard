package com.backyard.backyard;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Report> {

public ListAdapter(Context context, int textViewResourceId) {
    super(context, textViewResourceId);
    // TODO Auto-generated constructor stub
}

private List<Report> items;

public ListAdapter(Context context, int resource, List<Report> items) {

    super(context, resource, items);

    this.items = items;

}

@Override
public View getView(int position, View convertView, ViewGroup parent) {

    View v = convertView;

    if (v == null) {

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        v = vi.inflate(R.layout.reportslist, null);

    }

    Report p = items.get(position);

    if (p != null) {

        TextView tt = (TextView) v.findViewById(R.id.id);
        TextView tt1 = (TextView) v.findViewById(R.id.categoryId);
        TextView tt3 = (TextView) v.findViewById(R.id.description);
        TextView tt4 = (TextView) v.findViewById(R.id.photoview);
        TextView tt5 = (TextView) v.findViewById(R.id.audioview);
        TextView tt6 = (TextView) v.findViewById(R.id.videoview);

        if (tt != null) {
            tt.setText(p.getSector());
        }
        if (tt1 != null) {

            tt1.setText(p.getCompany());
        }
        if (tt3 != null) {

            tt3.setText(p.getDescription());
        }
        if (tt4 != null) {
        	String photo = p.getPhoto();
        	
          if(photo != null)
          {
        	  if (photo.isEmpty()){} else {
        	  tt4.setText("Photo Attached");}
          }
            
        }
        if (tt5 != null) {
        	String audio = p.getAudio();
            if(audio != null)
            {
          	  if (audio.isEmpty()){} else {
            	  tt5.setText("Audio Attached");}
          	  //tt5.setText(audio);
            }
        }
        if (tt6 != null) {
        	String video = p.getVideo();
            if(video != null)
            {
          	  if (video.isEmpty()){} else {
          		  
            	  tt6.setText("Video attached");}
            }
        }
        
    }

    return v;

}
}
