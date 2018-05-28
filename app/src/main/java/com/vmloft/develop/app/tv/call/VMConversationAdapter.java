package com.vmloft.develop.app.tv.call;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMTextMessageBody;
import com.vmloft.develop.library.tools.utils.VMLog;
import java.util.List;

/**
 * Created by lzan13 on 2017/3/29.
 *
 * 历史通话适配器
 */
public class VMConversationAdapter
        extends RecyclerView.Adapter<VMConversationAdapter.ConversationViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<EMConversation> list;
    private ItemListener itemListener;

    public VMConversationAdapter(Context context, List<EMConversation> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_contacts_card, parent, false);
        ConversationViewHolder viewHolder = new ConversationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ConversationViewHolder holder, final int position) {
        EMConversation conversation = list.get(position);
        holder.avatarView.setImageResource(R.mipmap.ic_launcher);
        holder.titleView.setText(conversation.conversationId());
        EMTextMessageBody body = (EMTextMessageBody) conversation.getLastMessage().getBody();
        holder.contentView.setText(body.getMessage());
        holder.timeView.setText("" + conversation.getLastMessage().getMsgTime());

        // 设置 item 点击监听
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                VMLog.d("The item view position: %d", position);
                if (itemListener != null) {
                    itemListener.onItemClick(holder.itemView, position);
                }
            }
        });
        // 设置 item 焦点变化监听
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                VMLog.d("The item view position: %d, focus: %b", position, hasFocus);
                if (itemListener != null) {
                    itemListener.onItemFocusChange(holder.itemView, hasFocus);
                }
            }
        });
    }

    @Override public int getItemCount() {
        return list.size();
    }

    /**
     * 设置 RecyclerView 事件监听接口
     */
    public interface ItemListener {
        /**
         * RecyclerView item 点击回调
         *
         * @param view 当前点击的 view
         * @param position 当前点击位置
         */
        void onItemClick(View view, int position);

        /**
         * RecyclerView item 焦点变化回调
         *
         * @param view 当前焦点变化的 view
         * @param hasFocus 是否获得焦点
         */
        void onItemFocusChange(View view, boolean hasFocus);
    }

    /**
     * 设置 RecyclerView item 事件监听回调
     */
    public void setItemListener(ItemListener listener) {
        itemListener = listener;
    }

    /**
     * 自定义会话列表项的 ViewHolder 用来显示会话列表项的内容
     */
    static class ConversationViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarView;
        TextView titleView;
        TextView contentView;
        TextView timeView;

        /**
         * 构造方法，初始化列表项的控件
         *
         * @param itemView item项的父控件
         */
        ConversationViewHolder(View itemView) {
            super(itemView);

            avatarView = (ImageView) itemView.findViewById(R.id.img_avatar);
            titleView = (TextView) itemView.findViewById(R.id.text_name);
            contentView = (TextView) itemView.findViewById(R.id.text_content);
            timeView = (TextView) itemView.findViewById(R.id.text_time);
        }
    }
}
