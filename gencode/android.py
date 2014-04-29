import config
import os
import shutil
from parsews import parsews

def typemap(t):
	if t == 'Date':
		return 'java.util.Date'
	return t

class parseandroid(parsews):

	beanpath = None
	servicepath = None
		
	def begin(self):
		path = config.path + config.package.replace('.','/')
		self.beanpath = path + '/bean/'
		self.servicepath = path + '/service/'
		try:
			shutil.rmtree(self.beanpath)
		except:
			pass
		os.makedirs(self.beanpath)
		try:
			shutil.rmtree(self.servicepath)
		except:
			pass
		os.makedirs(self.servicepath)

	def makebean(self,dict):
		fp = open(self.beanpath + dict['name'] + '.java', 'w')
		fp.write('package ' + config.package + '.bean;\n\n')
		fp.write('public class ' + dict['name'] + ' {\n')
		for field in dict['field']:
			fp.write('    public ' + typemap(field['type']) + ' ' + field['name'] + ';\n')
		fp.write('}\n')
		fp.close()
		
	def makegroup(self,dict):
		fp = open(self.servicepath + dict['name'] + '.java', 'w')
		fp.write('package ' + config.package + '.service;\n\n')
		fp.write('import cn.niven.web4app.Request;\n')
		fp.write('import ' + config.package + '.bean.*;\n\n')
		fp.write('public class ' + dict['name'] + ' {\n')
		for action in dict['action']:
			interface = action['name']
			nbody = interface[0].upper() + interface[1:]
			interface = 'On' + nbody + 'Handler'
			fp.write('\n    public static interface ' + interface +' {\n')
			fp.write('        public void on' + nbody + '(int errorCode, String errorMsg')
			if 'void' <> action['return']:
				fp.write(', ' + typemap(action['return']) + ' result')
			fp.write(');\n')
			fp.write('    }\n')
			fp.write('    public static Request<' + interface +'> ' + action['name'] + '(')
			first = True
			for param in action['parameter']:
				if not first:
					fp.write(', ')
				first = False
				fp.write(typemap(param['type']) + ' ' + param['name'])
			fp.write(') {\n')
			fp.write('        Request<' + interface + '> request = new Request<' + interface + '>();\n')
			fp.write('        request.url = "' + action['url'] + '";\n')
			fp.write('        request.resultInterfaceType = ' + interface + '.class;\n')
			fp.write('        request.resultType = ' + typemap(action['return']) + '.class;\n')
			for param in action['parameter']:
				fp.write('        if(null != ' + param['name'] + ')\n')
				fp.write('            request.params.put("' + param['name'] + '", ' + param['name'] + ');\n')
			fp.write('        \n')
			fp.write('        \n')
			fp.write('        request.fire();\n')
			fp.write('        return request;\n')
			fp.write('    }\n')
		fp.write('}\n')
		fp.close()
		
	def end(self):
		pass
		