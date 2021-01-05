package listview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import entity.Translate;

public class TranslateAdapter extends BaseAdapter {

	private Context context;
	private List<Translate> flashcardsetList;

	public TranslateAdapter(Context context, List<Translate> deviceList) {
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
        Translate flashcardset = flashcardsetList.get(position);
		View v = new TranslateAdapterView(this.context, flashcardset, position);
		return v;
	}

}
