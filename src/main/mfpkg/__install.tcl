##############################################################################
#                                                                            #
#                               Installer Script                             #
#                                      for                                   #
#                           Mediaflux Package: daris-web                     #
#                                                                            #
##############################################################################

set label [string toupper PACKAGE_$package]


#
# Set up authentication
#
#set domain www-public
set domain mflux
#set user www-public
set user public
if { [xvalue exists [authentication.domain.exists :domain ${domain}]] == "false" } {
	authentication.domain.create :domain ${domain}
	authentication.user.create :domain ${domain} :user ${user}
}


#
# Set up namespace
#
set namespace /www/daris-web
if { [xvalue exists [asset.namespace.exists :namespace ${namespace}]] == "true" } {
	puts "Installing package ${package} -- Destroying existing namespace: ${namespace}" 
	asset.namespace.destroy :namespace ${namespace}	
}
puts "Installing package ${package} -- Creating namespace: ${namespace}" 
asset.namespace.create :namespace -all true ${namespace} :description "the namespace for daris web application"

#
# Import web contents
#
puts "Installing package ${package} -- Importing web contents to namespace: ${namespace}"
asset.import :url archive:///www.zip :namespace ${namespace} :label -create yes ${label} :label PUBLISHED :update true


#
# Install as standalone web application: Set up HTTP processor
#
set url /daris-web
set entry_point DaRIS.html

# host is required if installing into non-default schema
if { [info exists host] == 0 } {
    set host ""
}
if { [xexists schema/name [schema.self.describe]] == 0 } {
    # in default schema. Ignore host arg.
    set host ""
}
set args ":url ${url}"
if { ${host} != "" } {
    set args "${args} :host ${host}"
}
if { [xvalue exists [eval "http.processor.exists $args"]] == "false" } {
	puts "Installing package ${package} -- Creating http processor: url=${url}"
	eval "http.processor.create ${args} \
		              :app daris \
		              :type asset \
		              :translate ${namespace} \
		              :authentication < :domain $domain :user $user > \
		              :entry-point ${entry_point}"
}