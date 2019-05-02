package at.fhv.transportClassifier.mainserver.bean.gis;

import java.util.regex.Pattern;
import javax.ejb.ScheduleExpression;

public class SchedulePlan {

  private String second;
  private String minute;
  private String hour;
  private String dayOfWeek;
  private String dayOfMonth;
  private String month;
  private String year;


  public String getSecond() {
    return second;
  }

  public String getMinute() {
    return minute;
  }

  public String getHour() {
    return hour;
  }

  public String getDayOfWeek() {
    return dayOfWeek;
  }

  public String getDayOfMonth() {
    return dayOfMonth;
  }

  public String getMonth() {
    return month;
  }

  public String getYear() {
    return year;
  }

  public void setSecond(String second){
    String[] split = second.split("(;|-)");
    for (String s : split) {
      boolean correct = testSecond(s);
      if(!correct){
        throw new IllegalArgumentException();
      }
    }
    this.second= second;
  }

  public void setMinute(String minute){
    String[] split = minute.split("(;|-)");
    for (String s : split) {
      boolean correct = testMinute(s);
      if(!correct){
        throw new IllegalArgumentException();
      }
    }
    this.minute = minute;
  }

  public void setHour(String hour){
    String[] split = hour.split("(;|-)");
    for (String s : split) {
      boolean correct = testHour(s);
      if(!correct){
        throw new IllegalArgumentException();
      }
    }
    this.hour = hour;
  }

  public void setDayOfWeek(String dayOfWeek){
    String[] split = dayOfWeek.split("(;|-)");
    for (String s : split) {
      boolean correct = testDayOfWeek(s);
      if(!correct){
        throw new IllegalArgumentException();
      }
    }
    this.dayOfWeek = dayOfWeek;
  }

  public void setDayOfMonth(String dayOfMonth){
    String[] split = dayOfMonth.split("(;|-)");
    for (String s : split) {
      boolean correct = testDayOfMonth(s);
      if(!correct){
        throw new IllegalArgumentException();
      }
    }
    this.dayOfMonth = dayOfMonth;
  }

  public void setYear(String year){
    String[] split = year.split("(;|-)");
    for (String s : split) {
      boolean correct = testYear(s);
      if(!correct){
        throw new IllegalArgumentException();
      }
    }
    this.year = year;
  }


  private boolean testSecond(String second){
    return Pattern.matches("^(([1-5]\\d)|(\\d)|\\*){1}$", second);
  }

  private boolean testMinute(String minute){
    return Pattern.matches("^(([1-5]\\d)|(\\d)|\\*){1}$", minute);
  }

  private boolean testHour(String hour){
    return Pattern.matches("^(([0-1]\\d)|(\\d)|(20)|(21)|(22)|(23)|(\\*)){1}$", hour);
  }

  private boolean testDayOfWeek(String dayOfWeek){
    return Pattern.matches("^([0-7]|(Sun)|(Mon)|(Tue)|(Wed)|(Thu)|(Fri)|(Sat)|(\\*)){1}$",dayOfWeek);
  }
  private boolean testDayOfMonth(String dayOfMonth){
    return Pattern.matches("^([0-2]\\d|(\\d)|(30)|(31)|(Sun)|(Mon)|(Tue)|(Wed)|(Thu)|(Fri)|(Sat)|(Last)|(1st)|(2nd)|(3rd)|([4-9]th)|([1-2]\\dth)|(30th)|(31th)|(\\*)){1}$",dayOfMonth);
  }

  private boolean testYear(String year){
    return Pattern.matches("^((2\\d{3})|(\\*)){1}$",year);
  }
  public ScheduleExpression generateScheduleExpression(){
    ScheduleExpression expression = new ScheduleExpression();
    if(minute!= null){
      expression.minute(minute);
    }
    if(second!= null){
      expression.second(second);
    }
    if(hour!= null){
      expression.hour(hour);
    }
    if(dayOfWeek!= null){
      expression.dayOfWeek(dayOfWeek);
    }
    if(dayOfMonth!= null){
      expression.dayOfMonth(dayOfMonth);
    } if(year!= null){
      expression.year(year);
    }
    return expression;


  }

}
