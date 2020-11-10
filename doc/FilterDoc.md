## AccessFilter Documentation
###### Last Updated: 17.06.202

Official Play Documentation: [Play Filters Documentation 2.8](https://www.playframework.com/documentation/2.8.x/Filters)

<br/>

Filters are defined in [application.conf](../conf/application.conf):

        play.de.innfactory.bootstrapplay2.filters.enabled = [
          "de.innfactory.bootstrapplay2.filters.logging.AccessLoggingFilter",
          "de.innfactory.bootstrapplay2.filters.access.RouteBlacklistFilter",
          "play.de.innfactory.bootstrapplay2.filters.cors.CORSFilter"
         ]

## Filters:  

### AccessLoggingFilter
[Go To File](../app/de/innfactory/bootstrapplay2/filters/logging/AccessLoggingFilter.scala)

- Logs all requests where the statusCode is contained in [application.conf](../conf/application.conf):

        logging.access.statusList = [404,403,401]
        logging.access.statusList = ${?LOGGING_STATUSLIST}

### RouteBlacklistFilter 

[Go To File](../app/de/innfactory/bootstrapplay2/filters/access/RouteBlacklistFilter.scala)

- Blocks all requests defined in RouteBlacklistFilter.scala



