package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeatherIconAdapter extends RecyclerView.Adapter<WeatherIconAdapter.ViewHolder> {

    private ArrayList<WeatherItem> items;
    private Context context;

    public WeatherIconAdapter(Context context){this.context = context;}

    public WeatherIconAdapter(){};

    public void setItems(ArrayList<WeatherItem> items){
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtWeatherIconHour.setText(items.get(position).getHour());
        holder.txtWeatherIconTemp.setText(items.get(position).getTemp());
        switch (items.get(position).getCondition()){
                case "Clouds":
                    holder.imgWeatherIcon.setImageResource(R.drawable.cloud);
                    break;
                case "Rain":
                    holder.imgWeatherIcon.setImageResource(R.drawable.rainy);
                    break;
                default: holder.imgWeatherIcon.setImageResource(R.drawable.sun);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtWeatherIconHour, txtWeatherIconTemp;
        private ImageView imgWeatherIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtWeatherIconHour = itemView.findViewById(R.id.txtWeatherIconHour);
            txtWeatherIconTemp = itemView.findViewById(R.id.txtWeatherIconTemp);
            imgWeatherIcon = itemView.findViewById(R.id.imgWeatherIcon);
        }
    }
}
