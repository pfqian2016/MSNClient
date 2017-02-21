package com.example.android.wifidirect.discovery;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.android.mobilesocietynetwork.client.R;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This fragment handles chat related UI which includes a list view for messages
 * and a message entry field with send button.
 */
public class WiFiChatFragment extends Fragment {

	final String FILE_NAME = "/example.txt";
	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	protected static final int CHOOSE_SCF_RESULT_CODE = 21;
	public static final String SYMBOL = "##";
	public static final String ACTION_SEND_FILE_HEAD_START = "SEND_FILE_HEAD_START";
	public static final String ACTION_SEND_FILE_HEAD_END = "SEND_FILE_HEAD_END";
	public static final String ACTION_SEND_FILE_END = "SEND_FILE_END_EOF";
	public static final String ACTION_SEND_TXT_HEAD_START = "SEND_TXT_HEAD_START";
	public static final String ACTION_SEND_TXT_HEAD_END = "SEND_TXT_HEAD_END";
	public static final String ACTION_SEND_TXT_END = "SEND_TXT_END";
	public static final String ACTION_SEND_SOURCEINFO = "com.example.android.wifidirect.SEND_SOURCEINFO";
	private String source, destination;
	private View view;
	private ChatManager chatManager;
	final Intent intent = new Intent();
	private TextView chatLine;
	private ListView listView;
	private ChatMessageAdapter adapter = null;
	private List<String> items = new ArrayList<String>();

	// private TXTLog.MyBinder binder;
	//
	// private ServiceConnection conn = new ServiceConnection() {
	// @Override
	// public void onServiceConnected(ComponentName name, IBinder service) {
	// System.out.println("--Service Connected--");
	// binder = (TXTLog.MyBinder) service;
	// }
	// @Override
	// public void onServiceDisconnected(ComponentName name) {
	// System.out.println("--Service Disconnected--");
	// }
	// };

	public WiFiChatFragment(String source, String destination) {
		this.source = source;
		this.destination = destination;
		// if (destination != null) {
		// System.out.println("public void onActivityResult source:" + source +
		// " destination:" + destination);
		// }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// final Intent intent = new Intent();
		// intent.setAction("com.example.SCFData.Service");
		// getActivity().bindService(intent, conn, Service.BIND_AUTO_CREATE);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_chat, container, false);
		chatLine = (TextView) view.findViewById(R.id.txtChatLine);
		listView = (ListView) view.findViewById(android.R.id.list);
		adapter = new ChatMessageAdapter(getActivity(), android.R.id.text1,
				items);
		listView.setAdapter(adapter);
		view.findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (chatManager != null) {
							String data = chatLine.getText().toString();
							int dataLength = data.length();
							String txt = ACTION_SEND_TXT_HEAD_START
									+ dataLength + ACTION_SEND_TXT_HEAD_END
									+ source + "?" + data + ACTION_SEND_TXT_END;
							chatManager.write(txt.getBytes());
							pushMessage("Me: " + chatLine.getText().toString());
							chatLine.setText("");
						}
					}
				});

		view.findViewById(R.id.button2).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("image/*");
						//Intent wrapperIntent = Intent.createChooser(intent,
						//		null);
						startActivityForResult(intent,//原来为wrapperIntent
								CHOOSE_FILE_RESULT_CODE);
					}
				});
		view.findViewById(R.id.button3).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("image/*");
					//	Intent wrapperIntent = Intent.createChooser(intent,
					//			null);
						startActivityForResult(intent,//改
								CHOOSE_SCF_RESULT_CODE);
					}
				});
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// User has picked an image. Transfer it to groContentResolver cr =
		// context.getContentResolver();up owner i.e peer using
		// FileTransferService.
		String head = null;
		Uri uri = data.getData();
		Context context = getActivity().getApplicationContext();
		Socket socket = chatManager.getSocket();
		ContentResolver cr = context.getContentResolver();
		try {
			InputStream inputStream = cr.openInputStream(Uri.parse(uri
					.toString()));
			String fileName = GetRealNameFromURI.getFilename(GetRealNameFromURI
					.getImageAbsolutePath(context, uri));
			int fileSize = inputStream.available();
			int headLength = 0;
			int time = (int) System.currentTimeMillis();
			if (requestCode == CHOOSE_FILE_RESULT_CODE) {
				head = WiFiChatFragment.ACTION_SEND_FILE_HEAD_START
						+ destination + SYMBOL + source + SYMBOL + fileName
						+ SYMBOL + time + SYMBOL + "LB1#LB2#LB3" + SYMBOL
						+ fileSize + WiFiChatFragment.ACTION_SEND_FILE_HEAD_END;
			} else {
				head = WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_START
						+ "test1"
						+ SYMBOL
						+ "test2"
						+ SYMBOL
						+ fileName
						+ SYMBOL
						+ time
						+ SYMBOL
						+ "LB1#LB2#LB3"
						+ SYMBOL
						+ fileSize
						+ WiFiServiceDiscoveryActivity.ACTION_SEND_SCFDATA_HEAD_END;
			}
			headLength = head.length();
			FileTransferThread fileTransfer = new FileTransferThread(
					this.getActivity(), socket, inputStream,
					(headLength + head));
			Thread thread = new Thread(fileTransfer);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface MessageTarget {
		public Handler getHandler();
	}

	public void setChatManager(ChatManager obj) {
		chatManager = obj;
	}

	public void pushMessage(String readMessage) {
		adapter.add(readMessage);
		adapter.notifyDataSetChanged();
		// binder.write(readMessage + "\r\n");
	}

	public class ChatMessageAdapter extends ArrayAdapter<String> {

		List<String> messages = null;

		public ChatMessageAdapter(Context context, int textViewResourceId,
				List<String> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(android.R.layout.simple_list_item_1, null);
			}

			String message = items.get(position);
			if (message != null && !message.isEmpty()) {

				TextView nameText = (TextView) v
						.findViewById(android.R.id.text1);
				if (nameText != null) {
					nameText.setText(message);
					if (message.startsWith("Me: ")) {
						nameText.setTextAppearance(getActivity(),
								R.style.normalText);
					} else {
						nameText.setTextAppearance(getActivity(),
								R.style.boldText);
					}
				}
			}
			return v;
		}
	}
}
