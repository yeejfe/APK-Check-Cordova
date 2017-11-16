del output-checkCordova.txt

for /r %%i in (vulnerable\*.*) do (
	java -jar checkCordova.jar "%%i" >> output-checkCordova.txt
	rmdir /s /q "%%~ni"
)
