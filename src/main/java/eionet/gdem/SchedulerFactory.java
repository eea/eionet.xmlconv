package eionet.gdem;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Properties;


//@Component("quartzSchedulerFactory")
public class SchedulerFactory { //extends SchedulerFactoryBean {

    private final DataSource quartzDataSource;


  //  @Autowired
    public SchedulerFactory(@Qualifier("quartzDataSource") DataSource quartzDataSource) {
        this.quartzDataSource = quartzDataSource;
    }

  //  @PostConstruct
    public void init() {
      //  this.setBeanName("jobScheduler");
     //   this.setDataSource(this.quartzDataSource);
    }

    public Scheduler getScheduler(){
     //   org.quartz.SchedulerFactory sf = new StdSchedulerFactory();
return null;
       // this.createScheduler(this,"test");
    }


}