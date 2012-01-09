package edu.ucdenver.bios.glimmpseweb.context;

public class FixedRandomMatrix
{
	private String name;
	private NamedMatrix fixedMatrix;
	private NamedMatrix randomMatrix;
	
	public FixedRandomMatrix(String name, NamedMatrix fixedMatrix, NamedMatrix randomMatrix)
	{
		this.name = name;
		this.fixedMatrix = fixedMatrix;
		this.randomMatrix = randomMatrix;
	}
	
	public String getName()
	{
		return name;
	}

	public NamedMatrix getFixedMatrix()
	{
		return fixedMatrix;
	}

	public NamedMatrix getRandomMatrix()
	{
		return randomMatrix;
	}

	
}
