<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Welcome to Grails</title>

    <asset:link rel="icon" href="favicon.ico" type="image/x-ico" />
</head>
<body>
  <h1><g:include action="index" /> </h1>
  <h1><g:include controller="foo" action="overridenExternalPath" /> </h1>
  <h1><g:include controller="fooPlugin" action="override" /> </h1>
  <h1><g:include controller="fooPlugin"  /> </h1>
</body>
</html>
