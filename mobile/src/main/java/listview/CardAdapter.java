package listview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import entity_display.MTranslate;

public class CardAdapter extends BaseAdapter {

	private Context context;
	private List<MTranslate> choiceList;

	public CardAdapter(Context context, List<MTranslate> deviceList) {
		this.context = context;
		this.choiceList = deviceList;
	}

	public int getCount() {
		return choiceList.size();
	}

	public Object getItem(int position) {
		return choiceList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
        MTranslate choice = choiceList.get(position);
		return new CardAdapterView(this.context, choice);

	}

}
