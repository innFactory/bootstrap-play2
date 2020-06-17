## AccessFilter Documentation
######Last Updated: 17.06.2020

Official Play Documentation: [Play Filters Documentation 2.8](https://www.playframework.com/documentation/2.8.x/Filters)

<br/>

Filters are defined in [application.conf](../conf/application.conf):

        play.filters.enabled = [
          "filters.logging.AccessLoggingFilter",
          "filters.access.RouteBlacklistFilter",
          "play.filters.cors.CORSFilter"
         ]

## Filters:  

### AccessLoggingFilter
[Go To File](../app/filters/logging/AccessLoggingFilter.scala)

- Logs all requests where the statusCode is contained in [application.conf](../conf/application.conf):

        logging.access.statusList = [404,403,401]
        logging.access.statusList = ${?LOGGING_STATUSLIST}

### RouteBlacklistFilter 

[Go To File](../app/filters/access/RouteBlacklistFilter.scala)

- Blocks all requests defined in RouteBlacklistFilter.scala



