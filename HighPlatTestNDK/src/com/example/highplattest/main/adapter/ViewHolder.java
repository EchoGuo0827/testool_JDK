package com.example.highplattest.main.adapter;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

public class ViewHolder {
    //被点击的当前位置
//    private int position;

    //用一个map集合来保存每个控件的id，这个SparseArray是android提供的一个比map使用效率更高的一个
    //集合，但是局限是，key只能是int类型，所以当键值对涉及到key是int类型时，可以优先考虑使用这个集合
    private SparseArray<View> array;

    //复用的布局
    private View convertView;


    //解析的布局资源id
//    private int layout;
    public ViewHolder(){

    }

    //带三个构造的方法,这里将构造方法私有，防止外界去创建，通过自身的静态方法去创建对象即可
    private ViewHolder(LayoutInflater mInflater,int position,int layout){
//        this.position = position;
        //每次创建对象，就将布局解析出来
        convertView = mInflater.inflate(layout,null);
        //然后将对象保存到convertView对应的setTag中，方便每次该获取
        convertView.setTag(this);
        array = new SparseArray<View>();
    }

    //通过这个方法，可以创建ViewHolder对象
    public static ViewHolder getHolder(View convertView, LayoutInflater mInflater, int position,int layout){
        //每次判断converView是否为空，如果为空就直接返回这个创建的对象
        if(convertView == null){
            return new ViewHolder(mInflater,position,layout);
        }else{
            //不为空的情况，就跟我们上面的代码一样，每次通过复用的控件拿到对应的ViewHolder对象
            ViewHolder holder = (ViewHolder)convertView.getTag();
            //这里一定要更新下下标的位置，虽然对象相同，但是我们每次都要更新现有的位置，
//            holder.position = position;
            //返回已经创建好的holder对象
            return holder;
        }
    }

    /**
    * 这个方法是通过控件id拿到对应的控件
    */
    @SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId){
        //每次通过viewId键去拿到到对应的控件
        View view =  array.get(viewId);
        //如果为空，表示该集合中还没有存入该控件
        if(view == null){
            //先要去通过converView拿到控件id
            view = convertView.findViewById(viewId);
            //保存到集合中，以便下次直接获取
            array.put(viewId,view);
        }
        //返回View的子类控件，采用泛型的方便是不需要强制转换了
        return (T)view;
    }
    //得到converView布局
    public View getConvertView(){
        return convertView;
    }
}