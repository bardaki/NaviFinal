package com.example.finalproject.custom;

import java.util.List;
import com.example.finalproject.R;
import com.example.finalproject.classes.Navigation;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FavoritesAdapter extends BaseAdapter {
	List<Navigation> data;
	Context context;

	public FavoritesAdapter(Context c, List<Navigation> favoritesArray){
		this.data = favoritesArray;
		this.context = c;	
	}

	@Override
	public int getCount() {
		if(data != null)
			return data.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position );
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" }) @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.favorites_item, null);
		TextView txtName = (TextView) view.findViewById(R.id.addressTxt);		
		txtName.setText(data.get(position).toString());

		return view;
	}

	public void remove(Object o){
		data.remove(o);
	}

	public void removeAll(){
		data.removeAll(data);
	}
}
