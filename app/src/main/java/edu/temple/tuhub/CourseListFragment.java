package edu.temple.tuhub;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.temple.tuhub.models.Term;
import edu.temple.tuhub.models.User;

public class CourseListFragment extends Fragment {

    private static final String ARG_TERM_INDEX = "term-index";
    private Term mTerm;

    CourseFragment.showCourseDetailsInterface activity;

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
        ListView mListView = (ListView) view.findViewById(R.id.course_list);
        mListView.setAdapter(new CourseListAdapter(getActivity().getApplicationContext(), mTerm.getCourses()));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                activity.showCourseDetails(mTerm.getCourses().get(i));
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (CourseFragment.showCourseDetailsInterface) c;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }
}
