package it.kdevgroup.storelocator;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.github.florent37.viewanimator.ViewAnimator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mattia on 07/04/16.
 */

public class StoresCardsAdapter extends RecyclerView.Adapter<StoresCardsAdapter.CardViewHolder> {

    private ArrayList<Store> stores;  //lista di eventi
    private Context ctx;

    private static final String TAG = "StoresCardsAdapter";

    public StoresCardsAdapter(ArrayList<Store> stores, Context ctx) {
        this.stores = stores;
        this.ctx = ctx;
        Collections.sort(this.stores);
    }

    /**
     * Chiamato quando il recycler view ha bisogno di una card per mostrare un evento
     *
     * @param viewGroup view padre di ogni carta (recyclerview in teoria)
     * @param viewType  tipo della view che sarà popolata (CardView)
     * @return oggetto CardViewHolder definito alla fine che setterà i vari TextView presenti nella CardView
     */
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
        return new CardViewHolder(v);
    }
    /**
     * Crea una card, chiamato ogni volta che deve essere mostrata una CardView
     *
     * @param cardHolder CardViewHolder restituito dal metodo precedente
     * @param position   posizione di un evento nella lista
     */
    @Override
    public void onBindViewHolder(final CardViewHolder cardHolder, final int position) {

        cardHolder.storeName.setText(stores.get(position).getName());
        cardHolder.storeAddress.setText(stores.get(position).getAddress());
        cardHolder.storePhone.setText(stores.get(position).getPhone());

        cardHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "preparazione activity di dettaglio");

                //40% di possibilità di steve jobs
                if (Math.random() < 0.4){
                    final AlertDialog dialog = new AlertDialog.Builder(ctx).create();

                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.steve);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    dialog.show();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            dialog.dismiss();

                            Intent vIntent = new Intent(ctx, DetailStoreActivity.class);
                            Bundle vBundle = new Bundle();
                            vBundle.putString(DetailStoreActivity.KEY_STORE, stores.get(position).getGUID());
                            vIntent.putExtras(vBundle);
                            ctx.startActivity(vIntent);
                        }
                    }, 35);
                } else {
                    Intent vIntent = new Intent(ctx, DetailStoreActivity.class);
                    Bundle vBundle = new Bundle();
                    vBundle.putString(DetailStoreActivity.KEY_STORE, stores.get(position).getGUID());
                    vIntent.putExtras(vBundle);
                    ctx.startActivity(vIntent);
                }

            }
        });

        if( !((HomeActivity)ctx).isNetworkAvailable() ){
            cardHolder.distance.setText(R.string.device_offline);
        }
        else if(stores.get(position).getLastKnownDistance() != 0) {
            cardHolder.distance.setText( Integer.toString(stores.get(position).getLastKnownDistance()) + " " + ctx.getResources().getString(R.string.from_you) );
        }

        ViewAnimator
                .animate(cardHolder.itemView)
                .bounceIn().interpolator(new BounceInterpolator())
                .wave().duration(2000)
                .start();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    public void swapStores(ArrayList<Store> newStores) {
        Log.i(TAG, "swapStores");

        stores = newStores;
        notifyDataSetChanged();
    }

    /**
     * "Contenitore" di ogni card
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView storeName;
        TextView storeAddress;
        TextView storePhone;
        TextView distance;

        CardViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.cardView);
            storeName = (TextView) itemView.findViewById(R.id.storeName);
            storeAddress = (TextView) itemView.findViewById(R.id.storeAddress);
            storePhone = (TextView) itemView.findViewById(R.id.storePhone);
            distance = (TextView) itemView.findViewById(R.id.storeDistance);
        }
    }
}