package listview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import entity.Pedometer;
import entity.Translate;

public class PedometerAdapter extends BaseAdapter {

	private Context context;
	private List<Pedometer> flashcardsetList;

	public PedometerAdapter(Context context, List<Pedometer> deviceList) {
		this.context = context;
		this.flashcardsetList = deviceList;
	}

	public int getCount() {
		return flashcardsetList.size();
	}

	public Object getItem(int position) {
		return flashcardsetList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        Pedometer flashcardset = flashcardsetList.get(position);
		View v = new PedometerAdapterView(this.context, flashcardset, position);
		return v;
	}

}
