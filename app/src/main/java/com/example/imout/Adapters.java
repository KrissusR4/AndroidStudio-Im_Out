package com.example.imout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class KorisnikAdapter extends ArrayAdapter<Korisnik> {
    Context context;
    Korisnik k[];

    KorisnikAdapter(Context c, Korisnik n[], int l){
        super(c, l, R.id.TextFriends, n);
        this.context = c;
        k = n;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.layout_friend, parent, false);
        ImageView imageView = row.findViewById(R.id.ImageFriends);
        TextView usernameView = row.findViewById(R.id.TextFriends);
        TextView nameView = row.findViewById(R.id.TextFriendsName);

        Picasso.get().load(k[position].getImage()).into(imageView);
        usernameView.setText(k[position].getUsername());
        nameView.setText(k[position].getName());

        return row;
    }
}
class IstorijaAdapter extends ArrayAdapter<Objects> {
    Context context;
    Objects k[];

    IstorijaAdapter(Context c, Objects n[], int l){
        super(c, l, R.id.textObjectName, n);
        this.context = c;
        k = n;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.layout_history, parent, false);
        ImageView imageView = row.findViewById(R.id.imageHistory);
        TextView nameView = row.findViewById(R.id.textObjectName);
        TextView dateView = row.findViewById(R.id.textObjectDate);

        Picasso.get().load(k[position].getLogo()).into(imageView);
        nameView.setText(k[position].getIme());
        dateView.setText(k[position].getDatum());

        return row;
    }
}
class RecenzijaAdapter extends ArrayAdapter<Rating> {
    Context context;
    Rating k[];

    RecenzijaAdapter(Context c, Rating n[], int l){
        super(c, l, R.id.textRatingName, n);
        this.context = c;
        k = n;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.layout_rating, parent, false);
        ImageView imageView = row.findViewById(R.id.imageRating);
        TextView nameView = row.findViewById(R.id.textRatingName);
        RatingBar ratingBar = row.findViewById(R.id.rating);

        Picasso.get().load(k[position].getLogo()).into(imageView);
        nameView.setText(k[position].getIme());
        ratingBar.setRating(k[position].getZvezdice());

        return row;
    }
}

class Objects{
    private int id;
    private String datum;
    private String logo;
    private String ime;

    Objects(int idd, String d, String l, String i){
        id = idd;
        datum = d;
        logo = "https://imoutcodebullies.000webhostapp.com/Images/Logo/".concat(l);
        ime = i;
    }
    public String getIme() {
        return ime;
    }

    public String getLogo() {
        return logo;
    }

    public String getDatum() {
        return datum;
    }

    public int getId() {
        return id;
    }
}
class Rating{
    private int zvezdice;
    private String comment;
    private String logo;
    private String ime;
    private int idLokalaRec;
    Rating(int z, String c, String lo, String i, int id){
        logo = "https://imoutcodebullies.000webhostapp.com/Images/Logo/".concat(lo);
        zvezdice = z;
        comment = c;
        ime = i;
        idLokalaRec = id;
    }

    public String getLogo() {
        return logo;
    }

    public String getIme() {
        return ime;
    }

    public String getComment() {
        return comment;
    }

    public int getZvezdice() {
        return zvezdice;
    }

    public int getIdLokalaRec() {
        return idLokalaRec;
    }
}
class Korisnik{
    private String username;
    private String name;
    private String image;
    Korisnik(String u, String n, String s, String i){
        username = u;
        name = n.concat(" ").concat(s);
        image = "https://imoutcodebullies.000webhostapp.com/Images/User/".concat(i);
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}

class Komentar {
    private String name;
    private int grade;
    private String comm;
    public Komentar(String name, int grade, String comm)
    {
        this.name = name;
        this.grade = grade;
        this.comm = comm;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public int getGrade()
    {
        return grade;
    }
    public void setGrade(int grade)
    {
        this.grade = grade;
    }
    public String getComm()
    {
        return comm;
    }
    public void setComm(String comm)
    {
        this.comm = comm;
    }
}
class KomentarAdapter extends ArrayAdapter<Komentar>
{
    Context context;
    Komentar k[];

    KomentarAdapter(Context c, Komentar n[], int l){
        super(c, l, n);
        this.context = c;
        k = n;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.layout_comments_pop, parent, false);
        TextView nameView = row.findViewById(R.id.ImeKomentatora);
        //TextView gradeView = row.findViewById(R.id.OcenaKomentatora);
        RatingBar gradeView = row.findViewById(R.id.OcenaKomentatora);
        TextView commView = row.findViewById(R.id.KomentarKomentatora);
        nameView.setText(k[position].getName());
        //gradeView.setText(k[position].getGrade());
        gradeView.setRating(k[position].getGrade());
        commView.setText(k[position].getComm());

        return row;
    }
}
class Notification{
    private String text1;
    private String text2;
    private String image;
    private String type;
    private int id;
    Notification(String t1, String t2, String i, String t, int idd){
        text1 = t1;
        text2 = t2;
        type = t;
        id = idd;
        if(type.equals("Korisnik"))
            image = "https://imoutcodebullies.000webhostapp.com/Images/User/".concat(i);
        else if(type.equals("Objekat"))
            image = "https://imoutcodebullies.000webhostapp.com/Images/Logo/".concat(i);
    }

    public String getImage() {
        return image;
    }

    public String getText1() {
        return text1;
    }

    public String getText2() {
        return text2;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
class NotificationAdapter extends ArrayAdapter<Notification>
{
    Context context;
    Notification k[];

    NotificationAdapter(Context c, Notification n[], int l){
        super(c, l, n);
        this.context = c;
        k = n;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.layout_friend, parent, false);
        ImageView imageView = row.findViewById(R.id.ImageFriends);
        TextView textView1 = row.findViewById(R.id.TextFriends);
        TextView textView2 = row.findViewById(R.id.TextFriendsName);

        Picasso.get().load(k[position].getImage()).into(imageView);
        textView1.setText(k[position].getText1());
        textView2.setText(k[position].getText2());

        return row;
    }
}

class Dogadjaj {
    private String name;
    private String time;
    private String date;
    private String band;
    private String price;
    private int idDog;
    public Dogadjaj(String name, String time, String date, String band, String price, int idDog)
    {
        this.name = name;
        this.time = time;
        this.date = date;
        this.band = band;
        this.price = price;
        this.idDog = idDog;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getTime()
    {
        return time;
    }
    public void setTime(String grade)
    {
        this.time = time;
    }
    public String getDate()
    {
        return date;
    }
    public void setDate(String date)
    {
        this.date = date;
    }
    public String getBand()
    {
        return band;
    }
    public void setBand(String band)
    {
        this.band = band;
    }
    public String getPrice()
    {
        return price;
    }
    public void setPrice(String price)
    {
        this.price = price;
    }
    public int getIdDog()
    {
        return idDog;
    }
    public void setIdDog(int idDog)
    {
        this.idDog = idDog;
    }
}
class DogadjajAdapter extends ArrayAdapter<Dogadjaj>
{
    Context context;
    Dogadjaj k[];

    DogadjajAdapter(Context c, Dogadjaj n[], int l){
        super(c, l, n);
        this.context = c;
        k = n;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.layout_event, parent, false);
        TextView nameView = row.findViewById(R.id.ImeDogadjaja);
        TextView timeView = row.findViewById(R.id.VremeDogadjaja);
        TextView dateView = row.findViewById(R.id.DatumDogadjaja);
        TextView bandView = row.findViewById(R.id.BendDogadjaja);
        TextView priceView = row.findViewById(R.id.CenaDogadjaja);
        nameView.setText(k[position].getName());
        timeView.setText(k[position].getTime());
        dateView.setText(k[position].getDate());
        bandView.setText(k[position].getBand());
        priceView.setText(k[position].getPrice());

        return row;
    }
}