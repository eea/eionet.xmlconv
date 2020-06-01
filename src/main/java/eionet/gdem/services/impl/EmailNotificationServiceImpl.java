package eionet.gdem.services;

import eionet.gdem.Properties;
import eionet.gdem.exceptions.EmailException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;


import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailNotificationServiceImpl implements EmailNotificationService{

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);

    /** Java mail sender. */
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public EmailNotificationServiceImpl() {
    }

    @Override
    public void sendNotificationForLongRunningJobs(List<String> jobIds) throws EmailException {
        LOGGER.info("Sending email notifications for long running jobs with ids: " + jobIds);
        //Create email and send it
        try {
            final String[] to = StringUtils.split(Properties.getStringProperty(Properties.longRunningJobEmailNotificationTo), ",");
            final String text = null;
            MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
                @Override
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false);
                    message.setText(text, false);
                    message.setFrom(new InternetAddress(Properties.getStringProperty(Properties.longRunningJobEmailNotificationFrom)));
                    message.setSubject("Site codes allocated");
                    message.setTo(to);
                }
            };
            mailSender.send(mimeMessagePreparator);

        } catch (Exception e) {
            throw new EmailException("Failed to send allocation notification: " + e.toString(), e);
        }

        /*
            SiteCodeAllocationNotification notification = new SiteCodeAllocationNotification();
            notification.setAllocationTime(allocationResult.getAllocationTime().toString());
            notification.setUsername(allocationResult.getUserName());
            notification.setCountry(country);
            notification.setNofAvailableCodes(Integer.toString(siteCodeDao.getFeeSiteCodeAmount()));
            notification.setTotalNofAllocatedCodes(Integer.toString(siteCodeDao.getCountryUnusedAllocations(country, false)));
            notification.setNofCodesAllocatedByEvent(Integer.toString(allocationResult.getAmount()));

            SiteCodeFilter filter = new SiteCodeFilter();
            filter.setDateAllocated(allocationResult.getAllocationTime());
            filter.setUserAllocated(allocationResult.getUserName());
            filter.setUsePaging(false);
            SiteCodeResult siteCodes = siteCodeDao.searchSiteCodes(filter);

            notification.setSiteCodes(siteCodes.getList());
            notification.setAdminRole(adminRole);

            final String[] to;
            // if test e-mail is provided, then do not send notification to actual receivers
            if (!StringUtils.isEmpty(Props.getProperty(PropsIF.SITE_CODE_TEST_NOTIFICATION_TO))) {
                notification.setTest(true);
                notification.setTo(StringUtils.join(parseRoleAddresses(country), ","));
                to = StringUtils.split(Props.getProperty(PropsIF.SITE_CODE_TEST_NOTIFICATION_TO), ",");
            } else {
                to = parseRoleAddresses(country);
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("data", notification);

            final String text = processTemplate("site_code_allocation.ftl", map);

            MimeMessagePreparator mimeMessagePreparator = new MimeMessagePreparator() {
                @Override
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false);
                    message.setText(text, false);
                    message.setFrom(new InternetAddress(Props.getRequiredProperty(PropsIF.SITE_CODE_NOTIFICATION_FROM)));
                    message.setSubject("Site codes allocated");
                    message.setTo(to);
                }
            };
            mailSender.send(mimeMessagePreparator);

        } catch (Exception e) {
            throw new ServiceException("Failed to send allocation notification: " + e.toString(), e);
        }


         */


        LOGGER.info("Finished sending email notifications for long running jobs with ids: " + jobIds);
    }
}
