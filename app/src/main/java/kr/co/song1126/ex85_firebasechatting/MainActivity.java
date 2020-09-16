package kr.co.song1126.ex85_firebasechatting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    CircleImageView ivProfile;
    EditText etName;

    //프로필 이미지 Uri 참조변수
    Uri imgUri;

    boolean isChanged=false;//데이터의 변경이 있었는지 기본값은 false로 둔다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName=findViewById(R.id.et);
        ivProfile=findViewById(R.id.iv_profile);


        //이미 저장되어 있는 정보들 읽어오기
        loadData();//앱에 저장되어 있는 데이터 불러오기
        if (G.nickName!=null){// 저장된 데이터가 null값이 아니라면 실행(저장된 데이터가 있다
            etName.setText(G.nickName);
            Glide.with(this).load(G.profileUri).into(ivProfile);//G.profileUri에 저장된 데이터를 가져온다.
        }

    }


    public void clickImage(View view) {
        //이미지 눌렀을때
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imgUri=data.getData();
            if (imgUri != null) {
                Glide.with(this).load(imgUri).into(ivProfile);

                //프로필 이미지를 변경했다고 표식
                isChanged=true;
            }
        }

    }

    //데이터를 저장하는 메소드
    void saveData(){
        //프로필 이미지와 채팅명을 Firevase DB에 저장

        G.nickName=etName.getText().toString();

        // 스토리지가 아니라 DB에 저장하는 이유 : 상대방의

        //먼저 이미지 파일부터 Firebase Storage에 업로드

        //업로드할 파일명이 같지 않게 날짜를 이용해서 파일명 지정
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
        final String fileName=sdf.format(new Date())+".png";

        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        final StorageReference imgRef=firebaseStorage.getReference("profileImages/"+fileName);

        //파일참조 위치에 이미지 업로드
        UploadTask task=imgRef.putFile(imgUri);

        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {//스토리지에 사진이 업로드 되었다.
                //firebase 실시간 DB에 저장할 저장소에 업로드된 실제 인터넷 경로 URL을 알아내야 한다.

                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {// 다운로드 경로를 알아내는 리스너
                    @Override
                    public void onSuccess(Uri uri) {//(다운로드 uri경로 : firebase의 스토리지에 저장된 파일의 다운로드 경로)
                        //다운로드 URL을 G.prfilUri에 저장
                        G.profileUri=uri.toString();

                        //firebase에 저장된 이미지 파일의 경로를 fire DB에 저장 하고 내 디바이스에도 저장한다.
                        // (다른 디바이스에서 봐야하고 다음에 앱을 실행했을때 나의 프로필 사진을 그대로 갖고있어야 하기때문)

                        //1. firevase DB에 저장[ G.nickName, imgUri의 다운로드 URL]
                        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                        //'profiles'라는 이름의 자식노드 참조객체
                        DatabaseReference profileRef=firebaseDatabase.getReference("profiles");
                        //'nickName'을 Key 값으로 지정한 노드에 이미지 URL을 값으로 지정
                        profileRef.child(G.nickName).setValue(G.profileUri);

//===================================================================================================================

                        //todo SharedPreferences 연습하기.
                        //2. SharedPreferences 이용하여 저장[ G.nickName, G.profileUri ]
                        SharedPreferences pref=getSharedPreferences("account", MODE_PRIVATE);
                        SharedPreferences.Editor editor =pref.edit();//시작

                        editor.putString("nickName", G.nickName);       //저장할 내용
                        editor.putString("profileUrl", G.profileUri);   //저장할 내용

                        editor.commit();//끝

                        //모든 저장이 완료되었으므로...
                        //채팅화면으로 이동한다.
                        
                        //확인용 토스트
                        Toast.makeText(MainActivity.this, "저장완료", Toast.LENGTH_SHORT).show();

                        //누르면 채팅 액티비티로 이동
                        Intent intent=new Intent(MainActivity.this, ChattingActivity.class);
                        startActivity(intent);

                        finish();//로그인창 다시 안보이기.

                    }
                });
            }
        });


    }

    public void clickChat(View view) {
        //이미지 데이터가 변경되었는가?
        if (isChanged) saveData();
        else {//변경한 적이 없다면 바로 채팅화면으로 넘어간다.
            Intent intent =new Intent(this, ChattingActivity.class);
            startActivity(intent);
            finish();
        }

    }

    //디바이스에 저장된 정보 읽어 오기
    void loadData(){
        SharedPreferences pref=getSharedPreferences("account",MODE_PRIVATE);

        G.nickName=pref.getString("nickName",null);           //저장된 적 없으면 빈칸으로 둔다
        G.profileUri=pref.getString("profileUrl", null);      //없으면 비어있는 주소사용
    }

}
//이미지를 먼저 스토리지에 저장해야한다 실시간 데이터 베이스에는 경로만 저장가능
//스토리지에 이미지가 저장되어있어야 다른 사람이 볼 수 있다.