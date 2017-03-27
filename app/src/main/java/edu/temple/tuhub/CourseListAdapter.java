package edu.temple.tuhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.temple.tuhub.models.Course;

/**
 * Created by connorcrawford on 3/26/17.
 */

public class CourseListAdapter extends ArrayAdapter<Course> {

    List courses;

    public CourseListAdapter(Context context, List<Course> courses) {
        super(context, android.R.layout.simple_list_item_2, courses);
        this.courses = courses;
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Course course = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        // Lookup view for data population
        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
        // Populate the data into the template view using the data object
        text1.setText(course.getName());
        text2.setText(course.getTitle());
        // Return the completed view to render on screen
        return convertView;
    }

}
