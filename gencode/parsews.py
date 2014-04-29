import xml.dom.minidom
import urllib2


class parsews:

	document = None
	baseurl = ''
	
	def __init__(self,url):
		content = urllib2.urlopen(url).read()
		self.document = xml.dom.minidom.parseString(content)
	
	def start(self):
		self.begin()
		self.baseurl = self.document.getElementsByTagName('service')[0].getAttribute('baseurl')
		try:
			typesnode = self.document.getElementsByTagName('types')[0]
		except:
			typesnode = None
		if typesnode:
			for t in typesnode.getElementsByTagName('type'):
				typedict = {}
				typedict['name'] = t.getAttribute('name')
				typedict['field'] = []
				for f in t.getElementsByTagName('field'):
					field = {'name':f.getAttribute('name'),'type':f.getAttribute('type')}
					typedict['field'].append(field)
				self.makebean(typedict)
		try:
			actionsnode = self.document.getElementsByTagName('actions')[0]
		except:
			actionsnode = None
		if actionsnode:
			for g in actionsnode.getElementsByTagName('group'):
				groupdict = {}
				groupdict['name'] = g.getAttribute('name')
				groupdict['action'] = []
# 				try:
# 					groupdict['comment'] = g.getElementsByTagName('')
# 				except:
# 					pass
				for act in g.getElementsByTagName('action'):
					action = {}
					action['name'] = act.getAttribute('name')
					action['return'] = act.getAttribute('return')
					action['url'] = self.baseurl + act.getAttribute('path')
					action['parameter'] = []
					for p in act.getElementsByTagName('parameter'):
						param = {}
						param['name'] = p.getAttribute('name')
						param['type'] = p.getAttribute('type')
						action['parameter'].append(param)
					groupdict['action'].append(action)
				self.makegroup(groupdict)
		self.end()
