<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Triana Tool $toolname</title>
<!-- <link rel="stylesheet" href="styles.css" media="print" /> -->
</head>

<body>
	<div>
		<p>This is a tool instance page for the $toolname workflow. It creates a new instance of a task</p>
		<form action="$path" METHOD="POST">
		<input type="hidden" name="$toolname" value="yes!">
		<input type="submit" value="Run">
		</form>
	</div>

</body>
</html>