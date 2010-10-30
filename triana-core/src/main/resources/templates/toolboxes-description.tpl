<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Triana Toolboxes</title>
<link rel="stylesheet" href="$pathController.getResourcesRoot()css/Triana_style.css" type="text/css" />
<link rel="stylesheet" href="$pathController.getResourcesRoot()css/jquery.treeview.css" type="text/css" />

<script type="text/javascript" src="$pathController.getResourcesRoot()js/jquery.min.js"></script>
<script type="text/javascript" src="$pathController.getResourcesRoot()js/jquery.treeview.js"></script>
<script type="text/javascript">
  $(document).ready(function(){
    $("#toolboxes").treeview({
     collapsed: true
    });
  });
  </script>
</head>

<body>
	<div>
		<p>This is a toolboxes listing page</p>
	</div>
	<div>
            $toolboxes

    </div>
</body>
</html>
