mvn -Duse.google.eclipse.plugin=false gwt:eclipse

# after launch file is generated, you need to edit it, add "-noserver -nostartServer -nosuperDevMode -codeServerPort 9997" to org.eclipse.jdt.launching.PROGRAM_ARGUMENTS attribute, like below:
# <stringAttribute key="org.eclipse.jdt.launching.PROGRAM_ARGUMENTS" value="-war target/daris-web-1.0.2 -noserver -nostartServer -nosuperDevMode -codeServerPort 9997 -startupUrl DaRIS.html daris.web.DaRIS"/>