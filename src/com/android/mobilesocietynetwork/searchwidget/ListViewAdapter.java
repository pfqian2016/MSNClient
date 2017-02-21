package com.android.mobilesocietynetwork.searchwidget;

import java.util.ArrayList;
import java.util.List;
import com.android.mobilesocietynetwork.client.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter implements Filterable {

	private Context context;
	private List<String> listStr;
	private ListFilter filter;

	public ListViewAdapter(List<String> list, Context context) {
		this.listStr = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listStr.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listStr.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_view, null);
		}
		TextView firstname = (TextView) convertView.findViewById(R.id.item_community);
		firstname.setText(listStr.get(position));
		return convertView;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if (filter == null) {
			filter = new ListFilter(listStr);
		}
		return filter;
	}

	private class ListFilter extends Filter {

		private List<String> original;

		public ListFilter(List<String> list) {
			this.original = list;
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			FilterResults results = new FilterResults();
			if (constraint == null || constraint.length() == 0) {
				results.values = original;
				results.count = original.size();
			} else {
				List<String> mList = new ArrayList<String>();
				for (String s : original) {
					if (s.toUpperCase().contains(
							constraint.toString().toUpperCase())) {
						mList.add(s);
					}
				}
				results.values = mList;
				results.count = mList.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// TODO Auto-generated method stub
			listStr = (List<String>) results.values;
			notifyDataSetChanged();
		}

	}

}
