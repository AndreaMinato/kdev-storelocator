package it.kdevgroup.storelocator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PagerManager {

    public static class PagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = new String[] { "Mappa", "Negozi", "Terza roba" };
        private Context context;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int i) {
            return PageFragment.newInstance(i + 1);
            /*
            Fragment fragment = new PageFragment();
            Bundle args = new Bundle();
            switch(i){
                case 0:
                    //TODO lista negozi
                    // Our object is just an integer :-P
                    args.putInt(PageFragment.ARG_OBJECT, i + 1);
                    break;
                case 1:
                    //TODO mappa
                    // Our object is just an integer :-P
                    args.putInt(PageFragment.ARG_OBJECT, i + 1);
                    break;
                case 2:
                    //TODO boh non mi ricordo
                    // Our object is just an integer :-P
                    args.putInt(PageFragment.ARG_OBJECT, i + 1);
                    break;
                default:
                    break;
            }
            fragment.setArguments(args);

            return fragment;
            */
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class PageFragment extends Fragment {
        public static final String ARG_OBJECT = "object";
        private int section;

        public static PageFragment newInstance(int page) {
            Bundle args = new Bundle();
            args.putInt(ARG_OBJECT, page);
            PageFragment fragment = new PageFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            section = getArguments().getInt(ARG_OBJECT);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_test, container, false);
            TextView text = (TextView)rootView.findViewById(R.id.sectionText);
            text.setText("Section " + section);
            return rootView;
        }
    }

}