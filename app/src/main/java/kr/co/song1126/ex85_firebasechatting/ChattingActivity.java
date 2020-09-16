package kr.co.song1126.ex85_firebasechatting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class ChattingActivity extends AppCompatActivity {

    EditText et;
    ListView listView;
    ArrayList<MessageItem> messageItems =new ArrayList<>();
    ChatAdapter adapter;
    Button btn;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        //제목줄의 글씨는 보통 채팅방의 이름이지만... 상대방의 닉네임이다.
        getSupportActionBar().setTitle(G.nickName);

        et=findViewById(R.id.et_msg);
        listView=findViewById(R.id.listView);
        btn=findViewById(R.id.btn_send);
        btn.setClickable(false);//setEnabled


        adapter=new ChatAdapter(this, messageItems);
        listView.setAdapter(adapter);



        //firebase DB의 "chat"이라는 이름의 자식노드에 채팅데이터들 저장
        //"chat"이름을 변경하면 여러채팅방 제작이 가능하다.
        firebaseDatabase=FirebaseDatabase.getInstance();
        chatRef=firebaseDatabase.getReference("chat");


        //채팅 메세지의 변경내역에 반응하는 리스너 추가
        // 바뀐 정보만 갖고 싶지만 Value Event는 값 변경시 마다 전체 데이터를 다시 준다.
        //Child는 변경된 부분만 준다.
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //추가된 메세지 데이터 하나의 스냅샷을 준다
                MessageItem messageItem=dataSnapshot.getValue(MessageItem.class);

                //새로 추가된 아이템을 리스트에 추가
                messageItems.add(messageItem);
                adapter.notifyDataSetChanged();

                //리스트뷰의 커서위치를 가장 마지막 아이템 위치로
                listView.setSelection(messageItems.size()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et.getText().toString().equals("")){
                    btn.setClickable(false);
                }else {
                    btn.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void clickSend(View view) {
        //firebase DB에 저장할 데이터들 [ 닉네임, 메세지, 시간, 프로필이미지 URL ]
        String name=G.nickName;
        String message=et.getText().toString();
        String prfileUrl=G.profileUri;

        //메세지 작성 시간 문자열
        Calendar calendar=Calendar.getInstance();
        String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);

        //객체를 한번에 저장하기 위한 firebase의 기능사용
        MessageItem messageItem=new MessageItem(name, message, time, prfileUrl);

        chatRef.push().setValue(messageItem);//객체를 한번에 저장하는 기능
        et.setText("");

        //소프트 키패드를 안보이도록 하기
        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

    }
}
