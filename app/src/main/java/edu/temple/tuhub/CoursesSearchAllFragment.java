package edu.temple.tuhub;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Fragment;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.temple.tuhub.models.Entry;

public class CoursesSearchAllFragment extends Fragment {

    searchAllResultsInterface activity;
    private static final String ns = null;
    private boolean mAlreadyLoaded = false;
    String search = "";
    ListView lv;
    List courses;

    public CoursesSearchAllFragment() {
        // Required empty public constructor
    }
    public static CoursesSearchAllFragment newInstance() {
        CoursesSearchAllFragment fragment = new CoursesSearchAllFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            search = bundle.getString("query");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_courses_search_all, container, false);
        lv = (ListView) v.findViewById(R.id.courseSearchDisplay);

        if (savedInstanceState == null && !mAlreadyLoaded) {
            mAlreadyLoaded = true;
            fetchResults(search);
        }
        else{
            loadResults(courses);
        }
        return v;
    }


    @Override
    public void onAttach(Activity c) {
        super.onAttach(c);
        activity = (searchAllResultsInterface) c;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }


    public interface searchAllResultsInterface{
        void searchAllResultsInterface(Entry courseDetails);
    }

    private void fetchResults(final String query) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    URL addressURL = new URL("https://prd-mobile.temple.edu/CourseSearch/searchCatalog.jsp?searchTerms=" + query + "&term=201703&division=All&minRow=1&maxRow=100");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    addressURL.openStream()));
                    String response = "", tmpResponse;
                    tmpResponse = reader.readLine();
                    while (tmpResponse != null) {
                        response = response + tmpResponse;
                        tmpResponse = reader.readLine();
                    }
                    String xml = response;
                    Message msg = Message.obtain();
                    msg.obj = xml;
                    addressHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    Handler addressHandler = new Handler(new Handler.Callback() {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public boolean handleMessage(Message msg) {
            String responseObject = msg.obj.toString();
            try {
                courses = parse(new ByteArrayInputStream(responseObject.getBytes(StandardCharsets.UTF_8)));
                loadResults(courses);
               } catch (Exception e) {
                e.printStackTrace();
                try {
                    Toast.makeText(getActivity(), getString(R.string.noResults), Toast.LENGTH_SHORT).show();
                } catch(IllegalStateException e2){
                    e2.printStackTrace();
                }
            }
            // Toast.makeText(getActivity(),getString(R.string.invalidAddress),Toast.LENGTH_SHORT).show();
            return false;
        }
    });

    public void loadResults(List courses){
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        if(courses.size()==0){
            Toast.makeText(getActivity(),getString(R.string.noResults),Toast.LENGTH_SHORT).show();
            getActivity().getFragmentManager().popBackStack();
        }else {
            for (int i = 0; i < courses.size(); i++) {
                Map<String, String> datum = new HashMap<String, String>(2);
                datum.put("First Line", ((Entry) courses.get(i)).getCourse());
                datum.put("Second Line", ((Entry) courses.get(i)).getCrseId());
                data.add(datum);
                SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), data, android.R.layout.simple_list_item_2,
                        new String[]{"First Line", "Second Line"},
                        new int[]{android.R.id.text1, android.R.id.text2}) {

                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        text1.setTextColor(Color.BLACK);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                        text2.setTextColor(Color.DKGRAY);
                        return view;
                    }
                };
                final List tempCourses = courses;
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        activity.searchAllResultsInterface(((Entry)tempCourses.get(position)));
                    }
                });
                adapter.notifyDataSetChanged();
            }
        }
    }

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, ns, "courses");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the course tag
            if (name.equals("course")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }



    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "course");

        String course = null;
        String crseId = null;
        String description = null;
        String creditHr = null;
        String college = null;
        String division = null;
        String department = null;
        String schedule = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                course = readTitle(parser);
            } else if (name.equals("crseId")) {
                crseId = readCrseId(parser);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else if (name.equals("creditHr")) {
                creditHr = readCreditHr(parser);
            } else if (name.equals("college")) {
                college = readCollege(parser);
            } else if (name.equals("division")) {
                division = readDivision(parser);
            } else if (name.equals("department")) {
                department = readDepartment(parser);
            } else if (name.equals("schedule")) {
                schedule = readSchedule(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(course, crseId, description, creditHr, college, division, department, schedule);
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title.substring(title.indexOf('>') + 1, title.lastIndexOf('<'));
    }

    private String readCourse(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "course");
        String course = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "course");
        return course;
    }

    private String readCrseId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "crseId");
        String crseId = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "crseId");
        return crseId.substring(crseId.indexOf('>') + 1, crseId.lastIndexOf('<'));
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description.substring(description.indexOf('>') + 1, description.lastIndexOf('<'));
    }

    private String readCreditHr(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "creditHr");
        String creditHr = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("low")) {
                creditHr = readCredit(parser);
            } else {
                skip(parser);
            }
        }
        return creditHr;
    }

    private String readCredit(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readText(parser);
    }
    private String readCollege(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "college");
        String college = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "college");
        return college.substring(college.indexOf('>') + 1, college.lastIndexOf('<'));
    }
    private String readDivision(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "division");
        String division = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "division");
        return division.substring(division.indexOf('>') + 1, division.lastIndexOf('<'));
    }
    private String readDepartment(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "department");
        String department = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "department");
        return department;
    }
    private String readSchedule(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "schedule");
        String schedule = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.contains("column")) {
                schedule = readLecture(parser);
                //parser.next();
            } else {
                skip(parser);
            }
        }
        return schedule;
    }
    private String readLecture(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readText(parser);
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")){
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes summary tags in the feed.
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "summary");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "summary");
        return summary;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
