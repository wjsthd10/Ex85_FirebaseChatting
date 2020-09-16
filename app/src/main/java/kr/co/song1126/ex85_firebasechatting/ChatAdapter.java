package kr.co.song1126.ex85_firebasechatting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends BaseAdapter {

    Context context;
    ArrayList<MessageItem> messageItems;

    public ChatAdapter(Context context, ArrayList<MessageItem> messageItems) {
        this.context = context;
        this.messageItems = messageItems;
    }

    @Override
    public int getCount() {
        return messageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //뷰를 재활용 하면 안된다.

        MessageItem item=messageItems.get(position);

        //1. create view[ 만들 뷰 : mymsg, othermsg 중 하나]
        View itemView=null;

        if (G.nickName.equals(item.name)){
            itemView= LayoutInflater.from(context).inflate(R.layout.my_msgbox, parent, false);

        }else {
            itemView= LayoutInflater.from(context).inflate(R.layout.other_msgbox, parent, false);
        }


        //2. bind view

        CircleImageView iv=itemView.findViewById(R.id.iv);
        TextView tvName=itemView.findViewById(R.id.tv_name);
        TextView tvMsg=itemView.findViewById(R.id.tv_msg);
        TextView tvTime=itemView.findViewById(R.id.tv_time);

        Glide.with(context).load(item.profileUrl).into(iv);

        tvMsg.setText(item.message);
        tvName.setText(item.name);
        tvTime.setText(item.time);

        return itemView;
    }
}
