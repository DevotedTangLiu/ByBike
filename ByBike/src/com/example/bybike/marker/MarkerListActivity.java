package com.example.bybike.marker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.ab.activity.AbActivity;
import com.ab.view.pullview.AbPullListView;
import com.example.bybike.R;
import com.example.bybike.adapter.MarkerListAdapter;
import com.example.bybike.db.model.MarkerBean;

public class MarkerListActivity extends AbActivity{

	private List<MarkerBean> myMarkerListData = null;
	private MarkerListAdapter myMarkerListAdapter = null;
	private AbPullListView myMarkerListView = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_marker_list);
        getTitleBar().setVisibility(View.GONE);
        
        myMarkerListView = (AbPullListView)findViewById(R.id.mListView);
		myMarkerListData = new ArrayList<MarkerBean>();
		myMarkerListAdapter = new MarkerListAdapter(MarkerListActivity.this, myMarkerListData);
		myMarkerListView.setAdapter(myMarkerListAdapter);
		myMarkerListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(MarkerListActivity.this, MarkerDetailActivity.class);
				i.putExtra("id", myMarkerListData.get(position-1).getMarkerId());
				startActivity(i);
				overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
			}
		});
		
		String dataString = getIntent().getExtras().getString("markerList");
		try {
			JSONArray dataArray = new JSONArray(dataString);
			for(int i = 0; i < dataArray.length(); i ++){
				JSONObject jo = dataArray.getJSONObject(i);
				MarkerBean mb = new MarkerBean();
				mb.setMarkerId(jo.getString("Id"));
				mb.setAddress(jo.getString("address"));
				mb.setCollectCount(jo.getString("collectCount"));
				mb.setLikeCount(jo.getString("likeCount"));
				mb.setCommentCount(jo.getString("commentCount"));
				mb.setMarkerName(jo.getString("name"));
				mb.setMarkerType(jo.getString("markerType"));
				
				myMarkerListData.add(mb);
			}
			
			myMarkerListAdapter.notifyDataSetChanged();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void onClick(View v){
    	switch (v.getId()) {
		case R.id.goBack:
			MarkerListActivity.this.finish();
			break;

		default:
			break;
		}
    }
    

}
