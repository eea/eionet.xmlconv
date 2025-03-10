package eionet.gdem.api.serverstatus.web;

import eionet.gdem.XMLConvException;
import eionet.gdem.api.serverstatus.web.service.ServerStatusObject;
import eionet.gdem.api.serverstatus.web.service.ServerStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Aris Katsanas<aka@eworx.gr>
 */
@RestController
public class ServerStatusRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStatusRestController.class);
    private static ServerStatusService serverStatusService ;
    
    @Autowired
    public ServerStatusRestController( ServerStatusService serverStatusService ) {
        this.serverStatusService = serverStatusService;
    }

    @RequestMapping(value="/serverstatus",method = RequestMethod.GET)
    public ResponseEntity<ServerStatusObject> returnServerStatus()throws XMLConvException{
    
        ServerStatusObject results = serverStatusService.getServerStatus();
                
        return new ResponseEntity <ServerStatusObject>( results ,HttpStatus.OK);
    }
    
    @ExceptionHandler(XMLConvException.class)
    public void HandleXMLConvException(XMLConvException exception, HttpServletResponse response) {
        LOGGER.error("ServerStatusRestContrl: " , exception);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
