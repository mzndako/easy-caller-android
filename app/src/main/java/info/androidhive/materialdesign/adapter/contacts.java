package info.androidhive.materialdesign.adapter;

/**
 * Created by HP ENVY on 5/18/2017.
 */


public class contacts {
    private String fname, mname, surname, rank, department, position, command, phone1, phone2, phone;
    private long starttime;

    public contacts() {
    }

    public contacts(String fname, String mname, String surname, String rank, String department, String position, String command, String phone1, String phone2) {
        this.fname = fname;
        this.mname = mname;
        this.surname = surname;
        this.rank = rank;
        this.department = department;
        this.position = position;
        this.command = command;
        this.phone1 = phone1;
        this.phone2 = phone2;
    }

    public String getName(){
        if(getMname().isEmpty()){
            return getSurname()+", "+getFname();
        }
        return getSurname()+", "+getFname()+ " "+getMname().substring(0,1)+".";
    }

    public String getPhoneCount(){
        if(getPhone2().isEmpty())
            return "1";
        return "2";
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String name) {
        this.fname = name;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String name) {
        this.mname = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String name) {
        this.surname = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String name) {
        this.rank = name;
    }

    public void setStartTime(long time){
        starttime = time;
    }

    public long getStartTime(){
        return starttime;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String name) {
        this.command = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String name) {
        this.department = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String name) {
        this.position = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String name) {
        this.phone = name;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String name) {
        this.phone1 = name;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String name) {
        this.phone2 = name;
    }


    @Override
    public String toString(){
        return getName()+getRank()+getDepartment()+getPosition()+getCommand()+getPhone1()+getPhone1();
    }

}
