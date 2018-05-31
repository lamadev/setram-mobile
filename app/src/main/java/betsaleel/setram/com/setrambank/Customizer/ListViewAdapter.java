package betsaleel.setram.com.setrambank.Customizer;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import betsaleel.setram.com.setrambank.R;
import betsaleel.setram.com.setrambank.pojos.Country;

/**
 * Created by hornellama on 30/12/2017.
 */

public class ListViewAdapter extends ArrayAdapter<Country> implements AdapterView.OnItemSelectedListener{
    private Context ctx=null;
    private List<Country>lst=null;
    public ListViewAdapter(@NonNull Context context, List<Country> listCountry) {
        super(context,0,listCountry);
        ctx=context;
        lst=listCountry;



    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_flags,parent, false);
        }

        ViewListCountry viewHolder = (ViewListCountry) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new ViewListCountry();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.language = (TextView) convertView.findViewById(R.id.language);
            viewHolder.Image = (ImageView) convertView.findViewById(R.id.imageView_flags);
            convertView.setTag(viewHolder);
        }

        //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
        Country country = getItem(position);

        //il ne reste plus qu'à remplir notre vue
        viewHolder.name.setText(country.getName());
        viewHolder.language.setText(country.getLanguage());
        viewHolder.Image.setImageResource(country.getImage_flags());

        return convertView;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class ViewListCountry{
        public TextView name;
        public TextView language;
        public ImageView Image;
    }
}
