package xzh.com.materialdesign.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import xzh.com.materialdesign.R;
import xzh.com.materialdesign.model.LittleOrderBean;
import xzh.com.materialdesign.model.Order_state;
import xzh.com.materialdesign.model.Orders;
import xzh.com.materialdesign.proxy.Command;
import xzh.com.materialdesign.ui.DetailsActivity;

/**
 * Created by xiangzhihong on 2016/3/2 on 15:41.
 */
public class HomeAdapter extends RecyclerView.Adapter<CellHolder> implements BaseAdapterInterface{

    private Context context;
    private List<LittleOrderBean> mList;
    JSONObject parameter;
    Orders orders;
    Order_state orderState;

    public HomeAdapter(Context context) {
       this.context=context;
        mList = new ArrayList<>();
    }

//    在任何ViewHolder被实例化的时候，OnCreateViewHolder将会被触发：
    @Override
    public CellHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.home_item, viewGroup, false);
        return new CellHolder(view);
    }

    //将list中的信息与每一个cellholder绑定在一起，cellholder就是一个view的封装，三个构造器都需要
    //不同的cellholder
    @Override
    public void onBindViewHolder(CellHolder cellHolder, int i) {
        final Integer integer=mList.get(i).getOrderId();
        initIntro(cellHolder,i);
       cellHolder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
//                    if(check())
               showDetail(integer);
           }
       });

    }


    private void showDetail(int orderId){
        Log.v("tb","showDetail");

        parameter=new JSONObject();
        try {

            parameter.put("type","OrderInfo");
            parameter.put("orderId", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        connect();

    }

    private void connect() {
        new Thread(){
            public void run() {
//                orders = (Orders) Proxy.getWebData(StateCode.OrderInfo,parameter);
                orders = (Orders) new Command().orderInfo(parameter);
                orderState = (Order_state)new Command().orderState(parameter);
//                orderState = (Order_state) Proxy.getWebData(StateCode.OrderState,parameter);
                Intent intent = new Intent(context, DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("order_info", orders);
                bundle.putSerializable("orderState", orderState);
                intent.putExtras(bundle);
                context.startActivity(intent);
            };
        }.start();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void add(Object s) {
        mList.add((LittleOrderBean) s);
        notifyDataSetChanged();
    }

    @Override
    public int getLastOrderId() {
        return mList.get(mList.size()-1).getOrderId();
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    //给第i个view绑定什么信息，主要从已经在外面注入的List获取对象的信息，并塞到cellholder内
    private void initIntro(CellHolder cellHolder, int i) {
        cellHolder.loadInfo(mList.get(i));
        cellHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DetailsActivity.class));
            }
        });
    }


}
