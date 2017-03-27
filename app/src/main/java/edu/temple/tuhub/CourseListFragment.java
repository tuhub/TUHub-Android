package edu.temple.tuhub;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import edu.temple.tuhub.models.Course;
import edu.temple.tuhub.models.Term;
import edu.temple.tuhub.models.User;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CourseListFragment extends Fragment {

    private static final String ARG_TERM_INDEX = "term-index";
    private OnListFragmentInteractionListener mListener;
    private Term mTerm;
    private ListView mListView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseListFragment() {
    }

    @SuppressWarnings("unused")
    public static CourseListFragment newInstance(int termIndex) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TERM_INDEX, termIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            if (User.CURRENT != null)
                mTerm = User.CURRENT.getTerms()[getArguments().getInt(ARG_TERM_INDEX)];
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        mListView = (ListView) view.findViewById(R.id.course_list);
        mListView.setAdapter(new CourseListAdapter(getContext(), mTerm.getCourses()));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Course item);
    }
}
