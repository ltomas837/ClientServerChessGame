

if [ "$#" -ne 1 ]; then
	echo "The number of inputted action(s) should be 1."
else
	if [ $1 = "compile"  ]; then
		cd src/java && gradle build && cd ../cpp && make all;
	else
		if [ $1 = "clean" ]; then
			cd src/java && gradle clean;
		else
			echo "Action not recognised. (compile/clean)";
		fi
	fi
fi
