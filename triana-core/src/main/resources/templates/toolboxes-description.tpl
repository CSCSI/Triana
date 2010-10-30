#parse ("header.template")
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
