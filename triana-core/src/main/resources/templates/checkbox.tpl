		<p>$checkbox.label
		<input type="checkbox"
		#if($checkbox.hasClasses())
		 class="$checkbox.getClassesAsString()"
		#end
		name="$checkbox.name" value="$checkbox.name"
		#if($checkbox.isChecked())
		 checked="checked"
		#end
		/>
		</p>

