package it.kdevgroup.storelocator;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by mattia on 07/04/16.
 */

public class EventsCardsAdapter extends RecyclerView.Adapter<EventsCardsAdapter.CardViewHolder> {

    private ArrayList<Store> stores;  //lista di eventi
    private Context ctx;
    private Location userLocation;

    private static final String TAG = "EventsCardsAdapter";

    public EventsCardsAdapter(ArrayList<Store> stores, Context ctx, Location userLocation) {
        this.stores = stores;
        this.ctx = ctx;
        this.userLocation = userLocation;
        Collections.sort(this.stores);
        Log.i(TAG, "stores sorted");
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
        cardHolder.storeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "preparazione activity di dettaglio");
                Intent vIntent = new Intent(ctx, DetailStoreActivity.class);
                Bundle vBundle = new Bundle();
                vBundle.putString(DetailStoreActivity.KEY_STORE, stores.get(position).getGUID());
                vIntent.putExtras(vBundle);
                ctx.startActivity(vIntent);
            }
        });
        cardHolder.storeAddress.setText(stores.get(position).getAddress());
        cardHolder.storeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mappa");

                // TODO: collegare alla mappa
            }
        });
        cardHolder.storePhone.setText(stores.get(position).getPhone());
        cardHolder.storePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "telefono");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + stores.get(position).getPhone()));
                ctx.startActivity(intent);
            }
        });

        if(stores.get(position).getLastKnownDistance() != 0)
            cardHolder.distance.setText(stores.get(position).getLastKnownDistance() + " km da te");
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