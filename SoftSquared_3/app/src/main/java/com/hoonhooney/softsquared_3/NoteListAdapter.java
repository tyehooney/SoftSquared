package com.hoonhooney.softsquared_3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hoonhooney.softsquared_3.activities.MainActivity;
import com.hoonhooney.softsquared_3.activities.NoteActivity;
import com.hoonhooney.softsquared_3.database.DBOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NoteListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Note> noteList;
    private ListView mParent;

    SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    public NoteListAdapter(Context context, List<Note> noteList){
        this.mContext = context;
        this.noteList = noteList;
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int i) {
        return noteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final Note note = noteList.get(i);

        final NoteViewHolder holder;

        final Animation down = AnimationUtils.loadAnimation(mContext, R.anim.item_slide_down);

        if (mParent == null)
            mParent = (ListView) viewGroup;

        // 캐시된 뷰가 없을 경우 새로 생성하고 뷰홀더를 생성
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_note, viewGroup, false);

            holder = new NoteViewHolder();

            holder.textView_title = view.findViewById(R.id.textView_title);
            holder.textView_details = view.findViewById(R.id.textView_details);
            holder.textView_last_edited = view.findViewById(R.id.textView_last_edited);
            holder.textView_created = view.findViewById(R.id.textView_created);

            holder.button_edit = view.findViewById(R.id.button_edit);
            holder.button_delete = view.findViewById(R.id.button_delete);

            holder.relativeLayout_item = view.findViewById(R.id.frameLayout_item);
            holder.linearLayout_details = view.findViewById(R.id.linearLayout_details);

            view.setTag(holder);
        }
        // 캐시된 뷰가 있을 경우 저장된 뷰홀더를 사용
        else{
            holder = (NoteViewHolder) view.getTag();
        }

        //제목, 내용
        holder.textView_title.setText(note.getTitle());
        holder.textView_details.setText(note.getDetails());

        //최초 작성 시간
        holder.textView_created.setText("최초 작성 : "+format.format(new Date(note.getId())));

        //최근 수정 시간
        Date lastEdited = note.getLastEdited();
        long gap = Math.abs(lastEdited.getTime() - System.currentTimeMillis());
        String lastEditedToString;

        if (gap < 60*60*1000)
            lastEditedToString = (gap/(60*1000))+"분 전";
        else if (gap < 24*60*60*1000)
            lastEditedToString = (gap/(60*60*1000))+"시간 전";
        else if (gap < 7*24*60*60*1000)
            lastEditedToString = (gap/(24*60*60*1000))+"일 전";
        else
            lastEditedToString = format.format(lastEdited);

        holder.textView_last_edited.setText(lastEditedToString);

        holder.linearLayout_details.setVisibility(note.isFocused() ? View.VISIBLE : View.GONE);

        //item onClickListener
        holder.relativeLayout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //click된 item을 제외한 나머지 -> 글 감추기
                for (int j = 0; j < noteList.size(); j++) {
                    if(noteList.get(j) != note && noteList.get(j).isFocused()){
                        //현재 화면에 보이는 item들에게만 적용
                        int visibleIndex = j - mParent.getFirstVisiblePosition();
                        if(visibleIndex >= 0){
                            View notFocusedView = mParent.getChildAt(visibleIndex);
                            Log.d("notFocusedView", "index : "+j+", childAt : "+mParent.getFirstVisiblePosition());

                            NoteViewHolder notFocusedViewHolder = (NoteViewHolder)notFocusedView.getTag();
                            notFocusedViewHolder.linearLayout_details.setVisibility(View.GONE);
                        }

                        noteList.get(j).setFocused(false);
                    }
                }

                if (!note.isFocused()){
                    holder.linearLayout_details.setVisibility(View.VISIBLE);
                    holder.linearLayout_details.startAnimation(down);
                    note.setFocused(true);
                }else{
                    holder.linearLayout_details.setVisibility(View.GONE);
                    note.setFocused(false);
                }
            }
        });

        //글 수정
        holder.button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NoteActivity.class);
                intent.putExtra("edit", true);
                intent.putExtra("id", note.getId());
                intent.putExtra("title", note.getTitle());
                intent.putExtra("details", note.getDetails());

                mContext.startActivity(intent);
            }
        });

        //글 삭제
        holder.button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("삭제")
                        .setMessage("이 노트를 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DBOpenHelper dbHelper = new DBOpenHelper(mContext);
                                dbHelper.open();
                                dbHelper.deleteColumn(note.getId());

                                noteList.remove(note);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "삭제 완료", Toast.LENGTH_SHORT).show();

                                ((MainActivity)mContext).onResume();
                            }
                        }).setNegativeButton("아니오", null)
                        .create()
                        .show();
            }
        });

        return view;
    }

    public class NoteViewHolder{
        private RelativeLayout relativeLayout_item;
        private LinearLayout linearLayout_details;
        private TextView textView_title, textView_details, textView_last_edited, textView_created;
        private ImageView button_edit, button_delete;
    }
}
