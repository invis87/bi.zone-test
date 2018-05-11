# Bi.Zone Test Task
Find multiple logins by ip address in a time window.

## Setup
To start the program you should have `sbt` and scala with java installed.

## Run (from SBT)
`sbt "run {path_to_csv_file} {time_window_long_millis}"`

Input csv should have format: `"{login_name}","{ip_address}","{login_time}"`

`login_time` should be in format: `yyyy-MM-dd HH:mm:ss`

You will find result CSV file in the same directory as `{path_to_csv_file}` with name `bi-zone_test_results.csv`

## Test
To start tests execute `sbt test` in root project folder