<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Form</title>
<!-- <link rel="stylesheet" href="styles.css" media="print" /> -->
</head>

<body>
	<div>
		<p>This is a form!</p>
		<form
		#if($form.hasClasses())
		 class="$form.getClassesAsString()"
		#end
		name="$form.name" action="$form.action" METHOD="$form.method">
        #foreach( $comp in $form.components)
            $comp.render()
        #end
		</form>
	</div>

</body>
</html>
