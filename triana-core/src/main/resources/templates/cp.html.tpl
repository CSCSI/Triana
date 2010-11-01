#parse ("header.template")
</head>

<body>
	<div id="container">
		<p>Class path for Toolbox $toolbox:</p>
		<ul>
		#foreach( $path in $paths)
            <li><a href="$path">$path</a></li>
        #end
        </ul>
	</div>
</body>
</html>
