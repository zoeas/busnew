package sub.search.bus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import businfo.activity.BusInfoActivity;

import com.zoeas.qdeagubus.MainActivity.OnBackAction;
import com.zoeas.qdeagubus.MyContentProvider;
import com.zoeas.qdeagubus.R;

/*  
 http://www.businfo.go.kr/bp/realInfo.do?act=detailInfo1&sysgbn=&routeId=3000564000&menu=0&routeNo=564&state=forward
 http://businfo.daegu.go.kr/ba/route/rtbspos.do?act=findByPos&routeId=3000564000&svcDt=20131029


 버스DB 
 _id  버스번호                   기점, 종점, 첫차시간, 막차시간, 배차간격, 즐겨찾기, 정방향역, 역방향역 
 506(테이블 버스DB1)   05:33   23:00   13

 전광판 뿌릴땐, 역번호 + 즐겨찾기로 기본틀을 뿌린후. 인터넷에서의 정보로 남은걸 뿌림

 역DB
 _id   역번호, 역이름, 경도, 위도, 즐겨찾기


 */

public class SearchBusNumberFragment extends ListFragment implements LoaderCallbacks<Cursor>, TextWatcher,
		OnKeyListener, OnBackAction {

	private Context context;
	private BusSearchListCursorAdapter busAdapter;
	private EditText et;
	private InputMethodManager imm;
	public static final String SELECTION_KEY = "selection";
	public static final String DEFAULT_STATION = "";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();
		View view = inflater.inflate(R.layout.fragment_search_bus_layout, null);
		// view.setOnTouchListener(this);

		et = (EditText) view.findViewById(R.id.edit_fragment_search_bus);
		et.addTextChangedListener(this);
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		et.setOnKeyListener(this);
		if (getArguments() != null) {
			et.setText(getArguments().getString(SELECTION_KEY));
		}

		imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

		busAdapter = new BusSearchListCursorAdapter(context, null, 0);
		setListAdapter(busAdapter);

		getLoaderManager().initLoader(0, null, this);
		return view;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {

		Uri uri = MyContentProvider.CONTENT_URI_BUS;
		String[] projection = { "_id", "bus_number", "bus_id" };
		String selection = null;
		String sortOrder = null;

		if (data != null) {
			selection = data.getString(SELECTION_KEY);
		}

		return new CursorLoader(context, uri, projection, selection, null, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		busAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		busAdapter.swapCursor(null);
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Bundle data = new Bundle();
		data.putString(SELECTION_KEY, "bus_number like '%" + s.toString() + "%'");

		getLoaderManager().restartLoader(0, data, this);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
			imm.hideSoftInputFromWindow(et.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			return true;
		}

		return false;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Cursor cursor = busAdapter.getCursor();
		cursor.moveToPosition(position);
		String busId = cursor.getString(2);
		String busNum = cursor.getString(1);

		Intent intent = new Intent(context, BusInfoActivity.class);
		intent.putExtra(BusInfoActivity.KEY_BUS_ID, busId);
		intent.putExtra(BusInfoActivity.KEY_BUS_NAME, busNum);
		intent.putExtra(BusInfoActivity.KEY_CURRENT_STATION_NAME, DEFAULT_STATION);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClear() {
		// TODO Auto-generated method stub

	}

	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// if(event.getAction() == MotionEvent.ACTION_DOWN){
	// // 에디터의 영역을 가져감
	// Rect rect = new Rect();
	// et.getGlobalVisibleRect(rect);
	//
	// Log.d("온터치","불려짐");
	//
	// if(!rect.contains((int)event.getRawX(), (int)event.getRawY())){
	// et.clearFocus();
	// imm.hideSoftInputFromWindow(et.getWindowToken(),
	// InputMethodManager.HIDE_NOT_ALWAYS);
	// }
	// }
	// return false;
	// }
	//

}
