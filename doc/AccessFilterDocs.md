## AccessFilter Documentation

Filters are defined in [application.conf](../conf/application.conf):

        play.filters.enabled = [
          "common.logging.AccessLoggingFilter",
          "play.filters.cors.CORSFilter"
         ]

### Filters:  

#### [AccessLoggingFilter](../app/common/logging/AccessLoggingFilter.scala)

- Logs all requests where the statusCode is contained in [application.conf](../conf/application.conf):

        logging.access.statusList = [404,403,401]
        logging.access.statusList = ${?LOGGING_STATUSLIST}
